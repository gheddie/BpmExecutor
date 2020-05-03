package de.gravitex.bpm.executor.handler;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.TaskHandler;

public class TaskT1Handler extends TaskHandler {
	
	@Override
	public void finishTask(Object processItem, Map<String, Object> variables, ProcessInstance processInstance) throws BpmExecutorException {
		Map<String, Object> myVariables = new HashMap<String, Object>();
		myVariables.put("value", "T2");
		super.finishTask(processItem, myVariables, processInstance);
	}
}