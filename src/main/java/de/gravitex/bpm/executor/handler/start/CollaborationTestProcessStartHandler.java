package de.gravitex.bpm.executor.handler.start;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.handler.base.IProcessStartHandler;
import de.gravitex.bpm.executor.util.HashMapBuilder;

public class CollaborationTestProcessStartHandler implements IProcessStartHandler {

	@Override
	public ProcessInstance startProcess(ProcessEngine processEngine, String processDefinitionKey, String businessKey) {
		return processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, businessKey,
				HashMapBuilder.create().withValues("VAR_MAINVAL", "M2").result());
	}
}