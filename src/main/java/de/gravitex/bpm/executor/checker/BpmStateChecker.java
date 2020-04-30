package de.gravitex.bpm.executor.checker;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public interface BpmStateChecker {

	void checkState() throws BpmExecutorException;
}