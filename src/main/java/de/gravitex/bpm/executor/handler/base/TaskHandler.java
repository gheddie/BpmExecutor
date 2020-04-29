package de.gravitex.bpm.executor.handler.base;

import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.task.Task;

public class TaskHandler extends ProcessItemHandler<Task> {
	
	private static final Logger logger = Logger.getLogger(TaskHandler.class);
	
	@Override
	public final void handleLifeCycleBegin(Object processItem) {
		finishTask(processItem, null);
	}

	public void finishTask(Object processItem, Map<String, Object> variables) {
		Task task = castProcessItem(processItem);
		taskService().complete(task.getId(), variables);
		logger.info("finished task: " + task.getName() + " [ID=" + task.getId() + "]...");
	}

	@Override
	public final void handleLifeCycle(Object processItem) {
		// ...
	}

	@Override
	public final void handleLifeCycleEnd(Object processItem) {
		// ...
	}

	@Override
	protected Task castProcessItem(Object processItem) {
		return (Task) processItem;
	}
}