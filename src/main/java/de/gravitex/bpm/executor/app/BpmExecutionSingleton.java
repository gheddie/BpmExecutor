package de.gravitex.bpm.executor.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.listener.IProcessEngineListener;
import de.gravitex.bpm.executor.app.listener.IProcessExecutionListener;
import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.enumeration.ExecutionPhase;
import de.gravitex.bpm.executor.enumeration.LifeCycle;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.EventSubscriptionHandler;
import de.gravitex.bpm.executor.handler.base.IProcessStartHandler;
import de.gravitex.bpm.executor.handler.base.MessageHandler;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import de.gravitex.bpm.executor.handler.base.TaskHandler;
import de.gravitex.bpm.executor.handler.base.TimerHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import de.gravitex.bpm.executor.util.DiffContainer;
import de.gravitex.bpm.executor.util.ProcessItemFormatter;
import de.gravitex.bpm.executor.util.StringUtil;

public class BpmExecutionSingleton implements IProcessEngineListener {
	
	private static final Logger logger = Logger.getLogger(BpmExecutionSingleton.class);

	private static BpmExecutionSingleton instance;

	private ProcessEngine processEngine;
	
	private IProcessExecutionListener processExecutionListener;
	
	private ProcessEngineListenerThread processEngineListenerThread;
	
	private static final ProcessExecutorSettings DEFAULT_EXECUTION_SETTINGS = ProcessExecutorSettings.fromValues(1000, false, true);

	private static final HashMap<Class<?>, ProcessItemHandler<?>> defaultProcessItemHandlers = new HashMap<Class<?>, ProcessItemHandler<?>>();
	static {
		defaultProcessItemHandlers.put(TaskEntity.class, new TaskHandler());
		defaultProcessItemHandlers.put(EventSubscriptionEntity.class, new EventSubscriptionHandler());
		// jobs
		defaultProcessItemHandlers.put(TimerEntity.class, new TimerHandler());
		defaultProcessItemHandlers.put(MessageEntity.class, new MessageHandler());
	}

	// process instance id -> process engine state
	HashMap<String, ProcessEngineState> deliveredEngineStates = new HashMap<String, ProcessEngineState>();

	private HashMap<String, ProcessExecutor> processExecutors = new HashMap<String, ProcessExecutor>();
	
	private HashMap<String, BpmDefinition> processDefinitions = new HashMap<String, BpmDefinition>();

	private HashMap<String, List<String>> processMessages = new HashMap<String, List<String>>();
	
	private List<String> commonMessages = new ArrayList<String>();

	private boolean locked = false;

	private BpmExecutionSingleton() {
		super();
	}

	public static BpmExecutionSingleton getInstance() {
		if (instance == null) {
			instance = new BpmExecutionSingleton();
			instance.init();
		}
		return instance;
	}

	private void init() {
		
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
				.setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1000").setJobExecutorActivate(true);
		processEngine = processEngineConfiguration.buildProcessEngine();
		processEngineListenerThread = new ProcessEngineListenerThread(this);
		processEngineListenerThread.start();
	}

	public void deployProcess(String aBpmFileName) {
		try {
			// TODO check if process is already deployed!!
			 processEngine.getRepositoryService().createDeployment().addClasspathResource(aBpmFileName).deploy();
		} catch (Exception e) {
			logger.error(e);
			// processExecutor.setProcessExecutorState(ProcessExecutorState.DEPLOYMENT_FAILED);
		}
	}

	public void deliverProcessState(ProcessEngineState newEngineState, ProcessExecutor processExecutor) throws BpmExecutorException {
		ProcessEngineState processEngineState = deliveredEngineStates.get(processExecutor.getProcessInstance().getId());
		if (processEngineState == null) {
			processEngineState = new ProcessEngineState();
		}
		HashMap<Class<?>, DiffContainer> diff = processEngineState.compareTo(newEngineState);
		evaluateChanges(diff, processExecutor.getProcessInstance());
		DiffContainer diffContainer = null;
		for (Class<?> clazz : diff.keySet()) {
			diffContainer = diff.get(clazz);
			for (LifeCycle lifeCycle : diffContainer.getItems().keySet()) {
				for (Object processItem : diffContainer.getItems().get(lifeCycle)) {
					ProcessItemHandler<?> handler = getItemHandler(processItem, processExecutor.getProcessInstance());
					if (handler == null) {
						throw new BpmExecutorException(
								"no handler found for process item type: " + processItem.getClass().getCanonicalName(), null, processExecutor.getProcessInstance());
					} else {
						logger.info("executing handler of class '" + handler.getClass().getSimpleName() + "' [execution counter="
								+ handler.getExecutionCounter() + "]...");
					}
					// logger.info("handling object: " + handler.format(processItem) + ", diff type: " + lifeCycle);
					switch (lifeCycle) {
					case CREATED:
						handler.handleLifeCycleBegin(processItem, processExecutor);
						break;
					case PERSISTENT:
						handler.handleLifeCycle(processItem, processExecutor);	
						break;
					case REMOVED:
						handler.handleLifeCycleEnd(processItem, processExecutor);
						break;
					}
				}
			}
		}
		deliveredEngineStates.put(processExecutor.getProcessInstance().getId(), newEngineState);
	}

	private void evaluateChanges(HashMap<Class<?>, DiffContainer> processItemChanges, ProcessInstance processInstance) {
		int changeCount = 0;
		for (Class<?> key : processItemChanges.keySet()) {
			changeCount += processItemChanges.get(key).getChangeCount();
		}
		if (changeCount == 0) {
			logger.warn(formatForProcessInstance("NO changes detected!!", processInstance));
		}
	}

	public String formatForProcessInstance(String message, ProcessInstance processInstance) {
		String processInstanceState = processInstance.isEnded() ? "ENDED" : "ACTIVE";
		return "[" + processInstance.getProcessDefinitionId() + " --> ID=" + processInstance.getId() + " --> "+processInstanceState+"] " + message;
	}

	private ProcessItemHandler<?> getItemHandler(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		String itemKey = ProcessItemFormatter.getKey(processItem);
		ProcessItemHandler<?> customHandler = processExecutors.get(processInstance.getId()).getHandler(itemKey);
		if (customHandler != null) {
			customHandler.increaseCounter();
			return customHandler;
		}
		ProcessItemHandler<?> processItemHandler = defaultProcessItemHandlers.get(processItem.getClass());
		if (processItemHandler == null) {
			throw new BpmExecutorException(
					"unable to aquire default item handler for type [" + processItem.getClass().getCanonicalName() + "]!!", null,
					processInstance);
		}
		return processItemHandler;
	}

	@Override
	public void fail(Exception e, ProcessInstance processInstance) {
		logger.error("execution for process [" + processInstance.getId() + "] failed: " + e.getMessage() + " ["
				+ e.getClass().getCanonicalName() + "]");
		processExecutors.get(processInstance.getId()).setProcessExecutorState(ProcessExecutorState.FAILED);
	}
	
	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public void invokeProcessStateChecker(Object processItem, ProcessInstance processInstance, ExecutionPhase executionPhase)
			throws BpmExecutorException {
		String itemKey = ProcessItemFormatter.getKey(processItem);
		logger.info("invoking process state checker for item '" + itemKey + "'...");
		BpmStateChecker bpmStateChecker = processExecutors.get(processInstance.getId()).getChecker(itemKey);
		if (bpmStateChecker != null) {
			bpmStateChecker.setProcessInstance(processInstance);
			switch (executionPhase) {
			case BEFORE_PROCESSING:
				bpmStateChecker.checkStateBeforeExecution(processInstance);				
				break;
			case AFTER_PROCESSING:
				bpmStateChecker.checkStateAfterExecution(processInstance);				
				break;
			}
		} else {
			logger.info("no bpm state checker found for key '" + itemKey + "'...");
		}
	}

	public BpmExecutionSingleton registerProcessDefinition(BpmDefinition bpmDefinition) {
		deployProcess(bpmDefinition.getBpmFileName());
		processDefinitions.put(bpmDefinition.getProcessDefinitionKey(), bpmDefinition);
		return this;
	}

	public synchronized void startProcess(String identifier) throws BpmExecutorException {
		
		if (locked) {
			logger.info("locked ---> returning!!");
			return;
		}
		
		BpmDefinition bpmDefinition = processDefinitions.get(identifier);
		
		if (bpmDefinition == null) {
			throw new BpmExecutorException("no bpm definition found for key '"+identifier+"'!!", null, null);
		}
		
		try {
			String businessKey = UUID.randomUUID().toString();
			
			ProcessInstance processInstance = null;
			
			IProcessStartHandler processStartHandler = bpmDefinition.getProcessStartHandler();
			if (processStartHandler != null) {
				processInstance = processStartHandler.startProcess(processEngine, bpmDefinition.getProcessDefinitionKey(), businessKey);
			} else {
				processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(bpmDefinition.getProcessDefinitionKey(),
						businessKey);
			}
			
			logger.info(formatForProcessInstance("generated business key: " + businessKey, processInstance));
			
			ProcessExecutor processExecutor = new ProcessExecutor(bpmDefinition);
			
			processExecutor.setStartDate(new Date());
			
			processExecutor.setBusinessKey(businessKey);
			processExecutor.setProcessInstance(processInstance);
			
			processExecutor.setProcessExecutorState(ProcessExecutorState.RUNNING);
			processExecutors.put(processInstance.getId(), processExecutor);	
		} catch (Exception e) {
			logger.error(e);
			putMessage(null, "Unable to start process '" + identifier + "': " + e.getMessage(), e);
		}
	}
	
	public ProcessExecutorSettings getProcessExecutorSettings(ProcessInstance processInstance) {
		ProcessExecutorSettings processExecutorSettings = processExecutors.get(processInstance.getId()).getBpmDefinition().getProcessExecutorSettings();
		if (processExecutorSettings == null) {
			return DEFAULT_EXECUTION_SETTINGS;
		}
		return processExecutorSettings;
	}

	public Collection<BpmDefinition> getBpmDefinitions() {
		return processDefinitions.values();
	}

	public List<ProcessExecutor> getProcessExecutors(boolean onlyRunning) {
		
		List<ProcessExecutor> result = null;
		if (onlyRunning) {
			result = new ArrayList<ProcessExecutor>();
			for (ProcessExecutor processExecutor : processExecutors.values()) {
				if (processExecutor.getProcessExecutorState().equals(ProcessExecutorState.RUNNING)) {
					result.add(processExecutor);
				}
			}
		} else {
			result = new ArrayList<ProcessExecutor>(processExecutors.values());
		}
		for (ProcessExecutor processExecutor : result) {
			if (processExecutor.getProcessExecutorState().equals(ProcessExecutorState.RUNNING)) {
				processExecutor.setActivity(getCurrentProcessActivities(processExecutor));	
			}
		}
		return result;
	}

	private String getCurrentProcessActivities(ProcessExecutor processExecutor) {
		
		if (!processExecutor.getProcessExecutorState().equals(ProcessExecutorState.RUNNING)) {
			logger.warn("requested current process activities for no more running process [ID="
					+ processExecutor.getProcessInstance().getId() + "]");
			return null;
		}
		logger.info("retrieving current process activities for process instance 'ID=" + processExecutor.getProcessInstance().getId()
				+ "' [state:" + processExecutor.getProcessExecutorState() + "]");
		List<String> activeActivityIds = null;
		String processInstanceId = processExecutor.getProcessInstance().getId();
		try {
			activeActivityIds = processEngine.getRuntimeService().getActiveActivityIds(processInstanceId);	
		} catch (Exception e) {
			logger.warn("unable to determine current activities for process instance id '" + processInstanceId + "'!!");
			return StringUtil.formatList(null);
		}
		return StringUtil.formatList(activeActivityIds);
	}
	
	public boolean executionEnded(ProcessInstance processInstance) {
		
		ProcessInstance lifeInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(processInstance.getId()).singleResult();
		HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
				.processInstanceId(processInstance.getId()).singleResult();
		return (historicProcessInstance != null && lifeInstance == null);
	}

	@Override
	public void checkExecutionEnded(ProcessExecutor processExecutor) {
		ProcessInstance processInstance = processExecutor.getProcessInstance();
		boolean executionEnded = executionEnded(processInstance);
		if (executionEnded) {
			logger.info("process instance ["+processInstance.getId()+"] has ENDED!!");
		} else {
			logger.info("process instance ["+processInstance.getId()+"] has NOT ENDED!!");
		}
		if (executionEnded) {
			processExecutor.setProcessExecutorState(ProcessExecutorState.FINISHED);
			if (processExecutionListener != null) {
				processExecutionListener.processFinished();	
			}
		}
	}

	public void putMessage(String processInstanceId, String message, Throwable t) {
		if (t != null) {
			message += " {" + t.getMessage() + "}";
		}
		if (processInstanceId == null) {
			commonMessages.add(message);
			return;
		}
		if (processMessages.get(processInstanceId) == null) {
			processMessages.put(processInstanceId, new ArrayList<String>());
		}
		processMessages.get(processInstanceId).add(message);
	}

	public List<String> getMessages(String processInstanceId) {
		return processMessages.get(processInstanceId);
	}

	@Override
	public void lock() {
		locked = true;
	}

	@Override
	public void unlock() {
		locked = false;
	}

	public List<String> getCommonMessages() {
		return commonMessages;
	}
	
	public BpmExecutionSingleton setProcessExecutionListener(IProcessExecutionListener aProcessExecutionListener) {
		this.processExecutionListener = aProcessExecutionListener;
		return this;
	}

	public List<String> getProcessPath(String processInstanceId) {
		return processExecutors.get(processInstanceId).getPathElements();
	}
}