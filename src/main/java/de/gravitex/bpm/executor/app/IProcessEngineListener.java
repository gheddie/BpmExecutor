package de.gravitex.bpm.executor.app;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.ProcessEngineState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public interface IProcessEngineListener {

	void deliverEngineState(ProcessEngineState processEngineState) throws BpmExecutorException;

	void fail(Exception e);

	void succeed();

	ProcessInstance getProcessInstance();

	boolean processesRunning();
}