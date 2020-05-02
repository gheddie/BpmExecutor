package de.gravitex.bpm.executor.app;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.listener.IProcessEngineListener;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class ProcessEngineListenerThread extends Thread {

	private static final Logger logger = Logger.getLogger(ProcessEngineListenerThread.class);

	private static final long DEFAULT_STEP_MILLIS = 1000;

	private IProcessEngineListener processEngineListener;

	public ProcessEngineListenerThread(IProcessEngineListener processEngineListener) {
		super();
		this.processEngineListener = processEngineListener;
	}

	public void run() {
		try {
			while (true) {
				ProcessExecutorSettings settings = BpmExecutionSingleton.getInstance().getProcessExecutorSettings();
				if (settings != null) {
					Thread.sleep(settings.getStepMillis());					
				} else {
					Thread.sleep(DEFAULT_STEP_MILLIS);
				}
				for (ProcessInstance processInstance : BpmExecutionSingleton.getInstance().getProcessInstances()) {
					ProcessEngineState engineState = generateEngineState(processInstance);
					processEngineListener.deliverEngineState(engineState, processInstance);	
				}
				if (!processEngineListener.processesRunning()) {
					logger.info("no more running process instances --> stopping execution thread!!");
					processEngineListener.succeed();
					stop();
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