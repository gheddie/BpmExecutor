package de.gravitex.bpm.executor.app.listener;

import de.gravitex.bpm.executor.app.ProcessExecutor;

/**
 * Used to listen to a {@link IProcessEngineListener}.
 * 
 * @author Sts
 *
 */
public interface IProcessExecutionListener {

	void processFinished(ProcessExecutor processExecutor);

	void processFailed(ProcessExecutor processExecutor);
}