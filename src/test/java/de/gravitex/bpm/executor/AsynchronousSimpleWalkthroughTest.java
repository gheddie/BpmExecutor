package de.gravitex.bpm.executor;

import org.junit.Test;

import de.gravitex.bpm.executor.app.BpmDefinition;
import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class AsynchronousSimpleWalkthroughTest {

	private static final String PROCESS_DEFINITION_ASYNCHRON_PROCESS = "AsynchronProcess";
	
	@Test
	public void testAsynchronousSimpleWalkthrough() {
		try {
			BpmExecutionSingleton.getInstance()
					.registerProcessDefinition(PROCESS_DEFINITION_ASYNCHRON_PROCESS, new BpmDefinition(null, "AsynchronProcess.bpmn", PROCESS_DEFINITION_ASYNCHRON_PROCESS))
					.startProcess(PROCESS_DEFINITION_ASYNCHRON_PROCESS);
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}