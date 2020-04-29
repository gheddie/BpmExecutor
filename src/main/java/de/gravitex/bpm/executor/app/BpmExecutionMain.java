package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	public static void main(String[] args) {

		try {
			ProcessExecutor.fromValues("SimpleTestProcess.bpmn", "SimpleTestProcess").withCustomHandler("TASK#T1", new TaskT1Handler())
					.withSettings(ProcessExecutorSettings.fromValues(10, false)).startProcess();
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}