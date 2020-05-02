package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.checker.TaskT1BpmChecker;
import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	public static void main(String[] args) {
		testSimpleProcess();
		// testAnotherProcess();
	}

	private static void testSimpleProcess() {
		try {
			ProcessExecutor processExecutor = ProcessExecutor.create()
					// deployments
					.addDeployment("SimpleTestProcess.bpmn")
					// settings
					.withSettings(ProcessExecutorSettings.fromValues(10, false, true, 500))
					// handlers
					.withCustomHandler("SimpleTestProcess", "TASK#T1", new TaskT1Handler())
					// checkers
					.withBpmStateChecker("SimpleTestProcess", "TASK#T1", new TaskT1BpmChecker());
			processExecutor.startProcess("SimpleTestProcess");
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}

	private static void testAnotherProcess() {
		try {
			ProcessExecutor processExecutor = ProcessExecutor.create()
					// deployments
					.addDeployment("SimpleTestProcess.bpmn")
					.addDeployment("AnotherProcess.bpmn")
					// settings
					.withSettings(ProcessExecutorSettings.fromValues(10, false, true, 500))
					// checkers
					.withBpmStateChecker("AnotherProcess", "TASK#TX", new TaskTxBpmChecker());
			processExecutor.startProcess("AnotherProcess");
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}