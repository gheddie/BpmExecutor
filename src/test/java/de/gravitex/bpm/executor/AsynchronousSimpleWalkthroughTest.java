package de.gravitex.bpm.executor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.gravitex.bpm.executor.app.BpmnDefinition;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.TaskT1Handler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import de.gravitex.bpm.executor.util.ProcessTestContainer;
import de.gravitex.bpm.executor.util.StringUtil;

public class AsynchronousSimpleWalkthroughTest {

	@Test
	public void testAsynchronousSimpleWalkthrough() {

		ProcessTestContainer container = null;
		try {
			container = new ProcessTestContainer(
					new BpmnDefinition(ProcessExecutorSettings.fromValues(1000, true, true), "SimpleTestProcess.bpmn", "SimpleTestProcess")
							.withCustomHandler("TASK#T1", new TaskT1Handler()));
			container.startProcess();
			assertEquals("[MSG_ONE,TASK_T1,TASK_T2,Timer1,MSG_TWO,TASK_T3]", StringUtil.formatList(container.getProcessPath()));
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
	}
}