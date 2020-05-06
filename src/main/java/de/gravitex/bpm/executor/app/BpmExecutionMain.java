package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.checker.TaskT1BpmChecker;
import de.gravitex.bpm.executor.checker.TaskTxBpmChecker;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.handler.TaskTMHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	private static final ProcessExecutorSettings DEFAULT_SETTINGS = ProcessExecutorSettings.fromValues(100, false, true);

	public static void main(String[] args) {
		try {
			BpmExecutionSingleton.getInstance()
					.registerProcessDefinition(new BpmDefinition(DEFAULT_SETTINGS, "SimpleTestProcess.bpmn", "SimpleTestProcess")
							.withCustomHandler("TASK#T1", new TaskT1Handler()).withBpmStateChecker("TASK#T1", new TaskT1BpmChecker()));
			BpmExecutionSingleton.getInstance()
					.registerProcessDefinition(new BpmDefinition(DEFAULT_SETTINGS, "AnotherProcess.bpmn", "AnotherProcess")
							.withBpmStateChecker("TASK#TX", new TaskTxBpmChecker()));
			BpmExecutionSingleton.getInstance()
					.registerProcessDefinition(new BpmDefinition(DEFAULT_SETTINGS, "LoopProcess.bpmn", "LoopProcess")
							.withCustomHandler("TASK#TM", new TaskTMHandler()));
			BpmExecutionSingleton.getInstance().startProcess("SimpleTestProcess");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}