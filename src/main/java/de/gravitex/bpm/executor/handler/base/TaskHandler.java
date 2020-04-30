package de.gravitex.bpm.executor.handler.base;

import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class TaskHandler extends ProcessItemHandler<Task> {
	
	private static final Logger logger = Logger.getLogger(TaskHandler.class);
	
	@Override
	public final void handleLifeCycleBegin(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		finishTask(processItem, null, processInstance);
		invokeProcessStateChecker(castProcessItem(processItem));
	}

	public void finishTask(Object processItem, Map<String, Object> variables, ProcessInstance processInstance) {
		Task task = castProcessItem(processItem);
		taskService().complete(task.getId(), variables);
		logger.info(formatForProcessInstance("finished task: " + task.getName() + " [ID=" + task.getId() + "]...", processInstance));
	}

	@Override
	public final void handleLifeCycle(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		// ...
	}

	@Override
	public final void handleLifeCycleEnd(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		// ...
	}

	@Override
	protected Task castProcessItem(Object processItem) {
		return (Task) processItem;
	}
}