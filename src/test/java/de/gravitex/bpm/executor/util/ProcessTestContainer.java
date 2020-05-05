package de.gravitex.bpm.executor.util;

import de.gravitex.bpm.executor.app.BpmDefinition;
import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.app.listener.IProcessExecutionListener;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import lombok.Data;

@Data
public class ProcessTestContainer implements IProcessExecutionListener {
	
	private static final String PROCESS_DEFINITION_ASYNCHRON_PROCESS = "AsynchronProcess";
	
	private boolean processFinished = false;

	@Override
	public void processFinished() {
		processFinished = true;
	}

	public void startProcess() {
		try {
			BpmExecutionSingleton.getInstance().setProcessExecutionListener(this)
					.registerProcessDefinition(PROCESS_DEFINITION_ASYNCHRON_PROCESS,
							new BpmDefinition(null, "AsynchronProcess.bpmn", PROCESS_DEFINITION_ASYNCHRON_PROCESS))
					.startProcess(PROCESS_DEFINITION_ASYNCHRON_PROCESS);
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
		while (!processFinished) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}
}