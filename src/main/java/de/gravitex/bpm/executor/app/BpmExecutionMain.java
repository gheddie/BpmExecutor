package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	public static void main(String[] args) {
		try {
			ProcessExecutor processExecutor = ProcessExecutor.create().addDeployment("SimpleTestProcess.bpmn")
					.addDeployment("AnotherProcess.bpmn").withSettings(ProcessExecutorSettings.fromValues(10, false, false, 1000))
					.withCustomHandler("SimpleTestProcess", "TASK#T1", new TaskT1Handler())
					.withBpmStateChecker("TASK#TX", new TaskTxBpmChecker());
			processExecutor.startProcess("SimpleTestProcess", 5);
			processExecutor.startProcess("AnotherProcess", 2);
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}