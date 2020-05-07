package de.gravitex.bpm.executor.app.listener;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.enumeration.ExecutionPhase;

/**
 * Used to listen to a {@link IProcessEngineListener}.
 * 
 * @author Sts
 *
 */
public interface IProcessExecutionListener {

	void processFinished(ProcessExecutor processExecutor);

	void processFailed(ProcessExecutor processExecutor);

	void itemFinalized(Object processItem, ProcessInstance processInstance, ExecutionPhase executionPhase);
}