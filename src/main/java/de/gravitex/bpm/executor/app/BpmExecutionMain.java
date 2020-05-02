package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.checker.TaskT1BpmChecker;
import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.handler.TaskTMHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	private static final ProcessExecutorSettings DEFAULT_SETTINGS = ProcessExecutorSettings.fromValues(10, false, true);

	public static void main(String[] args) {
		try {
			BpmExecutionSingleton instance = BpmExecutionSingleton.getInstance();
			instance.handleProcessExecutor(
					ProcessExecutor.create("SimpleTestProcess.bpmn", "SimpleTestProcess").withCustomHandler("TASK#T1", new TaskT1Handler())
							.withBpmStateChecker("TASK#T1", new TaskT1BpmChecker()).withSettings(DEFAULT_SETTINGS));
			instance.handleProcessExecutor(ProcessExecutor.create("AnotherProcess.bpmn", "AnotherProcess")
					.withBpmStateChecker("TASK#TX", new TaskTxBpmChecker()).withSettings(DEFAULT_SETTINGS));
			instance.handleProcessExecutor(
					ProcessExecutor.create("LoopProcess.bpmn", "LoopProcess").withCustomHandler("TASK#TM", new TaskTMHandler()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}