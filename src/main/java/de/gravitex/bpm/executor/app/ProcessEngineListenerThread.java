package de.gravitex.bpm.executor.app;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.listener.IProcessEngineListener;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class ProcessEngineListenerThread extends Thread {

	private static final Logger logger = Logger.getLogger(ProcessEngineListenerThread.class);

	private static final long DEFAULT_STEP_MILLIS = 2000;

	private IProcessEngineListener processEngineListener;

	public ProcessEngineListenerThread(IProcessEngineListener processEngineListener) {
		super();
		this.processEngineListener = processEngineListener;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(DEFAULT_STEP_MILLIS);
				ProcessEngineState engineState = null;
				for (ProcessExecutor processExecutor : BpmExecutionSingleton.getInstance().getProcessExecutors()) {
					if (!processExecutor.getProcessExecutorState().equals(ProcessExecutorState.FINISHED)) {
						processEngineListener.checkExecutionEnded(processExecutor);
						engineState = generateEngineState(processExecutor.getProcessInstance());
						processEngineListener.deliverEngineState(engineState,
								processExecutor.getProcessInstance());	
					}
				}
			}
		} catch (InterruptedException e) {
			processEngineListener.fail(e, null);
		} catch (BpmExecutorException e) {
			processEngineListener.fail(e, e.getProcessInstance());
		}
	}

	private ProcessEngineState generateEngineState(ProcessInstance processInstance) {
		return new ProcessEngineState().fromProcessInstance(processInstance);
	}
}