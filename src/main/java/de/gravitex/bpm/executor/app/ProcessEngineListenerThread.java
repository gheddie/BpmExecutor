package de.gravitex.bpm.executor.app;

import org.apache.log4j.Logger;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class ProcessEngineListenerThread extends Thread {
	
	private static final Logger logger = Logger.getLogger(ProcessEngineListenerThread.class);
	
	private IProcessEngineListener processEngineListener;

	public ProcessEngineListenerThread(IProcessEngineListener processEngineListener) {
		super();
		this.processEngineListener = processEngineListener;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
				ProcessEngineState engineState = generateEngineState();
				if (!processEngineListener.processesRunning()) {
					logger.info("no more running process instances --> stopping execution thread!!");
					processEngineListener.succeed();
					stop();
				}
				processEngineListener.deliverEngineState(engineState);
			}
		} catch (InterruptedException e) {
			processEngineListener.fail(e);
		} catch (BpmExecutorException e) {
			processEngineListener.fail(e);
		}
	}

	private ProcessEngineState generateEngineState() {
		return new ProcessEngineState().fromProcessInstance(processEngineListener.getProcessInstance());
	}
}