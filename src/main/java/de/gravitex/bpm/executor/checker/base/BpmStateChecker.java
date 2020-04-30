package de.gravitex.bpm.executor.checker.base;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public abstract class BpmStateChecker {

	public void checkState() throws BpmExecutorException {
		BpmAsserter asserter = createBpmAsserter();
		asserter.assertState();
	}

	protected abstract BpmAsserter createBpmAsserter();
}