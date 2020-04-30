package de.gravitex.bpm.executor.handler.base;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public abstract class ProcessItemHandler<T> {
	
	private static final Logger logger = Logger.getLogger(ProcessItemHandler.class);

	protected abstract T castProcessItem(Object processItem);
	
	public abstract void handleLifeCycleBegin(Object processItem, ProcessInstance processInstance) throws BpmExecutorException;

	public abstract void handleLifeCycle(Object processItem, ProcessInstance processInstance) throws BpmExecutorException;

	public abstract void handleLifeCycleEnd(Object processItem, ProcessInstance processInstance) throws BpmExecutorException;
	
	// ------------------------------------------------------------------------------------
	// --------- services
	// ------------------------------------------------------------------------------------
	
	protected ManagementService managementService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getManagementService();
	}

	protected TaskService taskService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getTaskService();
	}

	protected RuntimeService runtimeService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getRuntimeService();
	}
	
	protected String formatForProcessInstance(String message, ProcessInstance processIstance) {
		return BpmExecutionSingleton.getInstance().formatForProcessInstance(message, processIstance);
	}
	
	protected void invokeProcessStateChecker(T finishedProcessItem, ProcessInstance processInstance) throws BpmExecutorException {
		BpmExecutionSingleton.getInstance().invokeProcessStateChecker(finishedProcessItem, processInstance);
	}
}