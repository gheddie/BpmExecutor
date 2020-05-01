package de.gravitex.bpm.executor.checker.base;

import java.util.List;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import lombok.Data;

@Data
public abstract class BpmStateChecker {
	
	private ProcessInstance processInstance;
	
	public void checkStateBeforeExecution(ProcessInstance processInstance) throws BpmExecutorException {
		// ...
	}

	public void checkStateAfterExecution(ProcessInstance processInstance) throws BpmExecutorException {
		// ...
	}
	
	// --- asserts
	
	protected BpmStateChecker assertTaskPresent(String taskDefinitionKey) throws BpmExecutorException {
		List<Task> moo = taskService().createTaskQuery().processInstanceId(getProcessInstance().getId()).list();
		Task task = taskService().createTaskQuery().processInstanceId(getProcessInstance().getId()).taskDefinitionKey(taskDefinitionKey).singleResult();
		if (task == null) {
			throw new BpmExecutorException("task " + taskDefinitionKey + " is not present!!", null);
		}
		return this;
	}
	
	protected BpmStateChecker assertWaitingAt(String activityId) throws BpmExecutorException {
		List<String> waitingActivityIds = runtimeService().getActiveActivityIds(getProcessInstance().getId());
		if (!waitingActivityIds.contains(activityId)) {
			throw new BpmExecutorException(
					"process instance [" + getProcessInstance().getId() + "] should be waiting at '" + activityId
							+ "' but it is not (only at [" + waitingActivityIds + "])!!",
					null);	
		}
		return this;
	}
	
	protected BpmStateChecker assertExecutionEnded() throws BpmExecutorException {
		HistoricProcessInstance historicProcessInstance = historyService().createHistoricProcessInstanceQuery().processInstanceId(getProcessInstance().getId()).singleResult();
		boolean ended = historicProcessInstance != null;
		if (!ended) {
			throw new BpmExecutorException("process instance [" + getProcessInstance().getId() + "] should be ended, but it is not" + "!!",
					null);
		}
		return this;
	}
	
	// --- services
	
	private HistoryService historyService() {
		return BpmExecutionSingleton.getInstance().getProcessEngine().getHistoryService();
	}
	
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