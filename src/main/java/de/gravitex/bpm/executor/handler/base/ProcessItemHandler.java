package de.gravitex.bpm.executor.handler.base;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.enumeration.ExecutionPhase;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import lombok.Data;

@Data
public abstract class ProcessItemHandler<T> {
	
	private static final Logger logger = Logger.getLogger(ProcessItemHandler.class);
	
	private int executionCounter = 0;

	protected abstract T castProcessItem(Object processItem);
	
	public abstract void handleLifeCycleBegin(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException;

	public abstract void handleLifeCycle(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException;

	public abstract void handleLifeCycleEnd(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException;
	
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
	
	protected void invokeProcessStateChecker(T finishedProcessItem, ProcessInstance processInstance, ExecutionPhase executionPhase)
			throws BpmExecutorException {
		BpmExecutionSingleton.getInstance().invokeProcessStateChecker(finishedProcessItem, processInstance, executionPhase);
	}

	public void increaseCounter() {
		executionCounter++;
	}
	
	protected void putMessage(ProcessInstance processInstance, String message, Throwable throwable) {
		BpmExecutionSingleton.getInstance().putMessage(processInstance != null ? processInstance.getId() : null, message, throwable);
	}
}