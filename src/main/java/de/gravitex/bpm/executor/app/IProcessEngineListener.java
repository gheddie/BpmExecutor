package de.gravitex.bpm.executor.app;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public interface IProcessEngineListener {

	void deliverEngineState(ProcessEngineState processEngineState, ProcessInstance processInstance) throws BpmExecutorException;

	void fail(Exception e);

	void succeed();

	boolean processesRunning();
}