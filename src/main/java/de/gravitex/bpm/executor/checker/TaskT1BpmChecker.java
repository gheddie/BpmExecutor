package de.gravitex.bpm.executor.checker;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class TaskT1BpmChecker extends BpmStateChecker {

	@Override
	public void checkStateBeforeExecution(ProcessInstance processInstance) throws BpmExecutorException {
		assertWaitingAt("TASK_T1");
	}

	@Override
	public void checkStateAfterExecution(ProcessInstance processInstance) throws BpmExecutorException {
		assertTaskPresent("TASK_T2");
	}
}