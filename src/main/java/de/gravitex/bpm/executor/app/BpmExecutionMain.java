package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	public static void main(String[] args) {
		try {
			ProcessExecutor processExecutor = ProcessExecutor.create().addDeployment("SimpleTestProcess.bpmn")
					.addDeployment("AnotherProcess.bpmn").withSettings(ProcessExecutorSettings.fromValues(10, false, true, 1000))
					.withCustomHandler("TASK#T1", new TaskT1Handler());
			// processExecutor.startProcess("SimpleTestProcess", 2);
			processExecutor.startProcess("AnotherProcess", 1);
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}