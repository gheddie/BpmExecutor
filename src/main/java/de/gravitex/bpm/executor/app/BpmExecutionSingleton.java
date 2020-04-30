package de.gravitex.bpm.executor.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.enumeration.LifeCycle;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.EventSubscriptionHandler;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import de.gravitex.bpm.executor.handler.base.TaskHandler;
import de.gravitex.bpm.executor.handler.base.TimerHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import de.gravitex.bpm.executor.util.DiffContainer;
import de.gravitex.bpm.executor.util.ProcessItemFormatter;

public class BpmExecutionSingleton implements IProcessEngineListener {
	
	private static final Logger logger = Logger.getLogger(BpmExecutionSingleton.class);

	private static BpmExecutionSingleton instance;

	private ProcessEngine processEngine;
	
	private ProcessEngineListenerThread processEngineListenerThread;
	
	private ProcessExecutorSettings processExecutorSettings;
	
	private static final HashMap<Class<?>, ProcessItemHandler<?>> defaultProcessItemHandlers = new HashMap<Class<?>, ProcessItemHandler<?>>();
	static {
		defaultProcessItemHandlers.put(TaskEntity.class, new TaskHandler());
		defaultProcessItemHandlers.put(TimerEntity.class, new TimerHandler());
		defaultProcessItemHandlers.put(EventSubscriptionEntity.class, new EventSubscriptionHandler());
	}
	
	private HashMap<String, ProcessItemHandler<?>> customProcessItemHandlers = new HashMap<String, ProcessItemHandler<?>>();

	// process instance id -> process engine state
	HashMap<String, ProcessEngineState> deliveredEngineStates = new HashMap<String, ProcessEngineState>();

	// process instance id -> process instance
	private HashMap<String, ProcessInstance> processInstances = new HashMap<String, ProcessInstance>();

	// process instance id -> business key
	private HashMap<String, String> businessKeys = new HashMap<String, String>();
	
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

	public void deployProcess(String processFileName) {
		try {
			processEngine.getRepositoryService().createDeployment().addClasspathResource(processFileName).deploy();
		} catch (Exception e) {
			fail(e);
		}
	}

	public BpmExecutionSingleton startProcessInstance(String processDefinitionKey, int count) {
		for (int i=0;i<count;i++) {
			String businessKey = UUID.randomUUID().toString();
			ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, businessKey);
			logger.info(formatForProcessInstance("generated business key: " + businessKey, processInstance));
			processInstances.put(processInstance.getId(), processInstance);
			businessKeys.put(processInstance.getId(), businessKey);			
		}
		return this;
	}

	public void deliverEngineState(ProcessEngineState newEngineState, ProcessInstance processInstance) throws BpmExecutorException {
		
		ProcessEngineState processEngineState = deliveredEngineStates.get(processInstance.getId());
		if (processEngineState == null) {
			processEngineState = new ProcessEngineState();
		}
		HashMap<Class<?>, DiffContainer> diff = processEngineState.compareTo(newEngineState);
		evaluateChanges(diff, processInstance);
		DiffContainer diffContainer = null;
		for (Class<?> clazz : diff.keySet()) {
			diffContainer = diff.get(clazz);
			for (LifeCycle lifeCycle : diffContainer.getItems().keySet()) {
				for (Object processItem : diffContainer.getItems().get(lifeCycle)) {
					ProcessItemHandler<?> handler = getItemHandler(processItem);
					if (handler == null) {
						throw new BpmExecutorException(
								"no handler found for process item type: " + processItem.getClass().getCanonicalName(), null);
					}
					// logger.info("handling object: " + handler.format(processItem) + ", diff type: " + lifeCycle);
					switch (lifeCycle) {
					case CREATED:
						handler.handleLifeCycleBegin(processItem, processInstance);
						break;
					case PERSISTENT:
						handler.handleLifeCycle(processItem, processInstance);	
						break;
					case REMOVED:
						handler.handleLifeCycleEnd(processItem, processInstance);
						break;
					}
				}
			}
		}
		deliveredEngineStates.put(processInstance.getId(), newEngineState);
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

	private ProcessItemHandler<?> getItemHandler(Object processItem) {
		String key = ProcessItemFormatter.getKey(processItem);
		ProcessItemHandler<?> customHandler = customProcessItemHandlers.get(key);
		if (customHandler != null) {
			return customHandler;
		}
		return defaultProcessItemHandlers.get(processItem.getClass());
	}

	@Override
	public void fail(Exception e) {
		logger.error("execution failed: " + e.getMessage() + " ["+e.getClass().getCanonicalName()+"]");
		System.exit(0);
	}

	@Override
	public void succeed() {
		logger.info("execution suceeded to end...");
		System.exit(0);
	}

	public BpmExecutionSingleton registerHandler(String key, ProcessItemHandler<?> value) {
		customProcessItemHandlers.put(key, value);
		return instance;
	}

	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	@Override
	public boolean processesRunning() {
		List<ProcessInstance> list = processEngine.getRuntimeService().createProcessInstanceQuery().list();
		return list.size() > 0;
	}
	
	public ProcessExecutorSettings getProcessExecutorSettings() {
		return processExecutorSettings;
	}
	
	public void setProcessExecutorSettings(ProcessExecutorSettings aProcessExecutorSettings) {
		this.processExecutorSettings = aProcessExecutorSettings;
	}

	public String getBusinessKey(ProcessInstance processInstance) {
		return businessKeys.get(processInstance.getId());
	}

	public Collection<ProcessInstance> getProcessInstances() {
		return processInstances.values();
	}
}