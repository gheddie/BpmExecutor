package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.handler.TaskTMHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;

public class BpmExecutionMain {

	private static final ProcessExecutorSettings DEFAULT_SETTINGS = ProcessExecutorSettings.fromValues(10, false, true);

	public static void main(String[] args) {
		try {
			
			/*
			BpmExecutionSingleton.getInstance()
					.handleProcessExecutor(ProcessExecutor.create("SimpleTestProcess.bpmn", "SimpleTestProcess")
							.withCustomHandler("TASK#T1", new TaskT1Handler()).withBpmStateChecker("TASK#T1", new TaskT1BpmChecker())
							.withSettings(DEFAULT_SETTINGS));
			BpmExecutionSingleton.getInstance().handleProcessExecutor(ProcessExecutor.create("AnotherProcess.bpmn", "AnotherProcess")
					.withBpmStateChecker("TASK#TX", new TaskTxBpmChecker()).withSettings(DEFAULT_SETTINGS));
					*/
			
			BpmExecutionSingleton.getInstance().handleProcessExecutor(ProcessExecutor.create("LoopProcess.bpmn", "LoopProcess").withCustomHandler("TASK#TM", new TaskTMHandler()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}