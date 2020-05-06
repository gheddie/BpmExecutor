package de.gravitex.bpm.executor.app.listener;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.ProcessEngineState;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public interface IProcessEngineListener {

	void deliverProcessState(ProcessEngineState processEngineState, ProcessExecutor processExecutor) throws BpmExecutorException;

	void fail(Exception e, ProcessInstance processInstance);

	void checkExecutionEnded(ProcessExecutor processExecutor);

	void lock();

	void unlock();
}