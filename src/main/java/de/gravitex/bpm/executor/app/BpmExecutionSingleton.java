package de.gravitex.bpm.executor.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	ProcessEngineState deliveredEngineState = new ProcessEngineState();

	private ProcessInstance processIstance;

	private String businessKey;
	
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

	/**
	 * @deprecated Use {@link #startProcessInstance(String)} instead
	 */
	public BpmExecutionSingleton startProcess(String processDefinitionKey) {
		return startProcessInstance(processDefinitionKey);
	}

	/**
	 * @deprecated Use {@link #startProcessInstance(String)} instead
	 */
	public BpmExecutionSingleton startProcessInstanc(String processDefinitionKey) {
		return startProcessInstance(processDefinitionKey);
	}

	public BpmExecutionSingleton startProcessInstance(String processDefinitionKey) {
		generateBusinessKey();
		processIstance = processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, businessKey);
		return this;
	}

	private void generateBusinessKey() {
		String result = UUID.randomUUID().toString();
		logger.info("generated business key: " + result);
		businessKey = result;
	}

	public void deliverEngineState(ProcessEngineState newEngineState) throws BpmExecutorException {
		
		HashMap<Class<?>, DiffContainer> diff = deliveredEngineState.compareTo(newEngineState);
		evaluateChanges(diff);
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
						handler.handleLifeCycleBegin(processItem);
						break;
					case PERSISTENT:
						handler.handleLifeCycle(processItem);	
						break;
					case REMOVED:
						handler.handleLifeCycleEnd(processItem);
						break;
					}
				}
			}
		}
		deliveredEngineState = newEngineState;
	}

	private void evaluateChanges(HashMap<Class<?>, DiffContainer> processItemChanges) {
		
		int changeCount = 0;
		for (Class<?> key : processItemChanges.keySet()) {
			changeCount += processItemChanges.get(key).getChangeCount();
		}
		if (changeCount == 0) {
			logger.warn("NO changes detected!!");
		}
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
	public ProcessInstance getProcessInstance() {
		return processIstance;
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

	public String getBusinessKey() {
		return businessKey;
	}
}