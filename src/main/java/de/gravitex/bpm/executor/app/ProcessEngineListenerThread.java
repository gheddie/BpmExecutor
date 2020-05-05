package de.gravitex.bpm.executor.app;

import java.util.Collection;

import org.apache.log4j.Logger;

import de.gravitex.bpm.executor.app.listener.IProcessExecutionListener;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class ProcessEngineListenerThread extends Thread {

	private static final Logger logger = Logger.getLogger(ProcessEngineListenerThread.class);

	private static final long DEFAULT_STEP_MILLIS = 2500;

	private IProcessExecutionListener processExecutionListener;

	public ProcessEngineListenerThread(IProcessExecutionListener processExecutionListener) {
		super();
		this.processExecutionListener = processExecutionListener;
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
			processExecutionListener.lock();
			Collection<ProcessExecutor> processExecutors = BpmExecutionSingleton.getInstance().getProcessExecutors();
			for (ProcessExecutor processExecutor : processExecutors) {
				if (processExecutor.getProcessExecutorState().equals(ProcessExecutorState.RUNNING)) {
					try {
						stepExecutor(processExecutor);
						processExecutionListener.stepSuceeded(processExecutor);
					} catch (Exception e) {
						logger.warn("caught exception in thread: " + e.getClass().getCanonicalName() + " --> failing executor.");
						if (e instanceof BpmExecutorException) {
							processExecutionListener.fail(e, processExecutor.getProcessInstance());	
						}
						processExecutionListener.unlock();			
						run();
					}
				}
			}
			processExecutionListener.unlock();
		}
	}

	private void handleThreadInterrupted() {
		// TODO Auto-generated method stub
	}

	private void stepExecutor(ProcessExecutor processExecutor) throws Exception {
		processExecutionListener.checkExecutionEnded(processExecutor);
		processExecutionListener.deliverProcessState(generateProcessState(processExecutor),
				processExecutor.getProcessInstance());
	}

	private ProcessEngineState generateProcessState(ProcessExecutor processExecutor) {
		return new ProcessEngineState().fromProcessInstance(processExecutor.getProcessInstance());
	}
}