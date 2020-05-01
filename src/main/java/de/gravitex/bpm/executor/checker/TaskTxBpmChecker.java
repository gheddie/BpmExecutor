package de.gravitex.bpm.executor.checker;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class TaskTxBpmChecker extends BpmStateChecker {
	
	@Override
	public void checkStateAfterExecution(ProcessInstance processInstance) throws BpmExecutorException {
		assertExecutionEnded();
	}
}