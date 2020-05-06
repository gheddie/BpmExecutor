package de.gravitex.bpm.executor.app.listener;

import de.gravitex.bpm.executor.app.ProcessEngineState;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public interface IProcessEngineListener {

	void deliverProcessState(ProcessEngineState processEngineState, ProcessExecutor processExecutor) throws BpmExecutorException;

	void fail(ProcessExecutor processExecutor, Throwable throwable);

	void checkExecutionEnded(ProcessExecutor processExecutor);

	void lock();

	void unlock();
}