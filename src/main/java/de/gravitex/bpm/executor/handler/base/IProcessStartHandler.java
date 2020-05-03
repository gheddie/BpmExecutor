package de.gravitex.bpm.executor.handler.base;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public interface IProcessStartHandler {

	ProcessInstance startProcess(ProcessEngine processEngine, String processDefinitionKey, String businessKey);
}