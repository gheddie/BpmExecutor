package de.gravitex.bpm.executor.handler.base;

import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.enumeration.ExecutionPhase;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class TaskHandler extends ProcessItemHandler<TaskEntity> {
	
	private static final Logger logger = Logger.getLogger(TaskHandler.class);
	
	@Override
	public final void handleLifeCycleBegin(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException {
		TaskEntity task = castProcessItem(processItem);
		logger.info("handling life cycle begin of task '"+task.getTaskDefinitionKey()+"'...");
		ProcessInstance processInstance = processExecutor.getProcessInstance();
		invokeProcessStateChecker(task, processInstance, ExecutionPhase.BEFORE_PROCESSING);
		finishTask(processItem, null, processExecutor);
		processExecutor.addPathElement(task.getTaskDefinitionKey());
		invokeProcessStateChecker(task, processInstance, ExecutionPhase.AFTER_PROCESSING);
	}

	public void finishTask(Object processItem, Map<String, Object> variables, ProcessExecutor processExecutor) throws BpmExecutorException {
		Task task = castProcessItem(processItem);
		ProcessInstance processInstance = processExecutor.getProcessInstance();
		try {
			taskService().complete(task.getId(), variables);
			String message = "finished task: " + task.getName() + " [ID=" + task.getId() + "]...";
			logger.info(formatForProcessInstance(message, processInstance));
			putMessage(processInstance, message, null);	
		} catch (Exception e) {
			String message = "unable to finish task '"+task+"'!!";
			putMessage(processInstance, message, e);
			throw new BpmExecutorException(message, e, processInstance);
		}
	}

	@Override
	public final void handleLifeCycle(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException {
		// ...
	}

	@Override
	public final void handleLifeCycleEnd(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException {
		// ...
	}

	@Override
	protected TaskEntity castProcessItem(Object processItem) {
		return (TaskEntity) processItem;
	}
}