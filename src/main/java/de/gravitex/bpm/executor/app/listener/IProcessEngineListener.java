package de.gravitex.bpm.executor.app.listener;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.ProcessEngineState;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public interface IProcessEngineListener {

	void deliverEngineState(ProcessEngineState processEngineState, ProcessInstance processInstance) throws BpmExecutorException;

	void fail(Exception e, ProcessInstance processInstance);

	void succeed();

	void checkExecutionEnded(ProcessExecutor processExecutor);
}