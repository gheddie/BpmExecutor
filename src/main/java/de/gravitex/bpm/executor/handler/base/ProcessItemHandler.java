package de.gravitex.bpm.executor.handler.base;

import java.util.List;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public abstract class ProcessItemHandler<T> {
	
	private static final Logger logger = Logger.getLogger(ProcessItemHandler.class);

	protected abstract T castProcessItem(Object processItem);
	
	public abstract void handleLifeCycleBegin(Object processItem);

	public abstract void handleLifeCycle(Object processItem);

	public abstract void handleLifeCycleEnd(Object processItem);
	
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
	
	// ------------------------------------------------------------------------------------
	// --------- asserts
	// ------------------------------------------------------------------------------------
	
	protected void assertTaskPresent(String taskDefinitionKey, int expectedTaskCount, ProcessEngine processEngine) throws BpmExecutorException {
		
		List<Task> taskList = processEngine.getTaskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey).list();
		if (!(taskList.size() == expectedTaskCount)) {
			throw new BpmExecutorException("invalid count for task [" + taskDefinitionKey + "], expected: " + expectedTaskCount
					+ ", found: " + taskList.size() + "!!", null);
		}
	}
	
	protected void assertRunningProcesses(int processCount, ProcessEngine processEngine) throws BpmExecutorException {
		if (!(processEngine.getRuntimeService().createExecutionQuery().list().size() == processCount)) {
			throw new BpmExecutorException("invalid count for task [" + processCount + "]!!", null);			
		}
	}
}