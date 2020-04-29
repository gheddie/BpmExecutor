package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	public static void main(String[] args) {
		try {
			ProcessExecutor processExecutor = ProcessExecutor.fromValues("SimpleTestProcess.bpmn", "SimpleTestProcess")
					.withSettings(ProcessExecutorSettings.fromValues(10, false, true, 1000))
					.withCustomHandler("TASK#T1", new TaskT1Handler());
			processExecutor.startProcess();
			processExecutor.startProcess();
			processExecutor.startProcess();
			processExecutor.startProcess();
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}