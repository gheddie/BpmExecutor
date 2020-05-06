package de.gravitex.bpm.executor.handler;

import java.util.HashMap;
import java.util.Map;

import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.TaskHandler;

public class TaskT1Handler extends TaskHandler {
	
	@Override
	public void finishTask(Object processItem, Map<String, Object> variables, ProcessExecutor processExecutor) throws BpmExecutorException {
		Map<String, Object> myVariables = new HashMap<String, Object>();
		myVariables.put("value", "T2");
		super.finishTask(processItem, myVariables, processExecutor);
	}
}