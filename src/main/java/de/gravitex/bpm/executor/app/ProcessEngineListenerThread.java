package de.gravitex.bpm.executor.app;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.listener.IProcessEngineListener;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class ProcessEngineListenerThread extends Thread {

	private static final Logger logger = Logger.getLogger(ProcessEngineListenerThread.class);

	private static final long DEFAULT_STEP_MILLIS = 5000;

	private IProcessEngineListener processEngineListener;

	public ProcessEngineListenerThread(IProcessEngineListener processEngineListener) {
		super();
		this.processEngineListener = processEngineListener;
	}

	public void run() {
		try {
			while (true) {
				logger.info("thread running...");
				Thread.sleep(DEFAULT_STEP_MILLIS);
				ProcessEngineState engineState = null;
				processEngineListener.lock();
				Set<ProcessExecutorState> distinctStates = new HashSet<ProcessExecutorState>();
				Collection<ProcessExecutor> processExecutors = BpmExecutionSingleton.getInstance().getProcessExecutors(true);
				for (ProcessExecutor processExecutor : processExecutors) {
					distinctStates.add(processExecutor.getProcessExecutorState());
					if (processExecutor.getProcessExecutorState().equals(ProcessExecutorState.RUNNING)) {
						processEngineListener.checkExecutionEnded(processExecutor);
						engineState = generateEngineState(processExecutor.getProcessInstance());
						processEngineListener.deliverEngineState(engineState,
								processExecutor.getProcessInstance());	
					}
				}
				/*
				if (!distinctStates.contains(ProcessExecutorState.RUNNING) && processExecutors.size() > 0) {
					logger.info("no more running executors --> stopping thread!!");
					stop();
				}
				*/
				processEngineListener.unlock();
			}
		} catch (Exception e) {
			logger.warn("caught exception in thread: " + e.getClass().getCanonicalName());
			if (e instanceof InterruptedException) {
				processEngineListener.fail(e, null);
			} else if (e instanceof BpmExecutorException) {
				processEngineListener.fail(e, ((BpmExecutorException) e).getProcessInstance());
			} else {
				String message = "unknown thread failure: " + e.getMessage();
				logger.error(message);
				BpmExecutionSingleton.getInstance().putMessage(null, message, e);
			}
			e.printStackTrace();
			logger.warn("resuming thread...");
			run();
		} finally {
			processEngineListener.unlock();
		}
	}

	private ProcessEngineState generateEngineState(ProcessInstance processInstance) {
		return new ProcessEngineState().fromProcessInstance(processInstance);
	}
}