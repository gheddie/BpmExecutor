package de.gravitex.bpm.executor.app;

import java.util.Collection;

import org.apache.log4j.Logger;

import de.gravitex.bpm.executor.app.listener.IProcessEngineListener;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class ProcessEngineListenerThread extends Thread {

	private static final Logger logger = Logger.getLogger(ProcessEngineListenerThread.class);

	private static final long DEFAULT_STEP_MILLIS = 2500;

	private IProcessEngineListener processEngineListener;

	public ProcessEngineListenerThread(IProcessEngineListener processEngineListener) {
		super();
		this.processEngineListener = processEngineListener;
	}
	
	public void run() {
		
		while (true) {
			logger.info("thread running...");
			try {
				Thread.sleep(DEFAULT_STEP_MILLIS);
			} catch (InterruptedException e) {
				logger.error("thread was interrupted: " + e);
				handleThreadInterrupted();
			}
			// loop running executors
			processEngineListener.lock();
			Collection<ProcessExecutor> processExecutors = BpmExecutionSingleton.getInstance().getProcessExecutors(true);
			for (ProcessExecutor processExecutor : processExecutors) {
				if (processExecutor.getProcessExecutorState().equals(ProcessExecutorState.RUNNING)) {
					try {
						stepExecutor(processExecutor);
					} catch (Exception e) {
						logger.warn("caught exception in thread: " + e.getClass().getCanonicalName() + " --> failing executor.");
						if (e instanceof BpmExecutorException) {
							processEngineListener.fail(e, processExecutor.getProcessInstance());	
						}
						processEngineListener.unlock();			
						run();
					}
				}
			}
			processEngineListener.unlock();
		}
	}

	private void handleThreadInterrupted() {
		// TODO Auto-generated method stub
	}

	private void stepExecutor(ProcessExecutor processExecutor) throws Exception {
		processEngineListener.checkExecutionEnded(processExecutor);
		processEngineListener.deliverEngineState(generateProcessState(processExecutor),
				processExecutor.getProcessInstance());
	}

	/**
	 * @deprecated Use {@link #generateProcessState(ProcessExecutor)} instead
	 */
	private ProcessEngineState generateEngineState(ProcessExecutor processExecutor) {
		return generateProcessState(processExecutor);
	}

	private ProcessEngineState generateProcessState(ProcessExecutor processExecutor) {
		return new ProcessEngineState().fromProcessInstance(processExecutor.getProcessInstance());
	}
}