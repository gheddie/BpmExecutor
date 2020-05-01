package de.gravitex.bpm.executor.checker.base;

import java.util.List;

import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import lombok.Data;

@Data
public abstract class BpmStateChecker {
	
	private ProcessInstance processInstance;

	public abstract void checkState(ProcessInstance processInstance) throws BpmExecutorException;
	
	// --- asserts
	
	protected void assertTaskPresent(String taskName, int taskCount) throws BpmExecutorException {
		List<Task> taskList = taskService().createTaskQuery().processInstanceId(getProcessInstance().getId()).list();
		if (!(taskList.size() == taskCount)) {
			throw new BpmExecutorException("task is not present at count ["+taskCount+"]!!", null);
		}
	}
	
	// --- services
	
	private TaskService taskService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getTaskService();
	}
	
	private RuntimeService runtimeService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getRuntimeService();
	}
	
	private ManagementService managementService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getManagementService();
	}
}