package de.gravitex.bpm.executor.app;

import java.util.HashMap;

import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.IProcessStartHandler;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import lombok.Data;

@Data
public class BpmnDefinition {

	private ProcessExecutorSettings processExecutorSettings;
	
	private HashMap<String, ProcessItemHandler<?>> customProcessItemHandlers = new HashMap<String, ProcessItemHandler<?>>();
	
	private HashMap<String, BpmStateChecker> bpmStateCheckers = new HashMap<String, BpmStateChecker>();
	
	private String bpmnFileName;
	
	private String processDefinitionKey;

	private IProcessStartHandler processStartHandler;
	
	public BpmnDefinition(ProcessExecutorSettings aProcessExecutorSettings, String aBpmnFileName, String aProcessDefinitionKey) {
		super();
		this.processExecutorSettings = aProcessExecutorSettings;
		this.bpmnFileName = aBpmnFileName;
		this.processDefinitionKey = aProcessDefinitionKey;
	}
	
	public BpmnDefinition withCustomHandler(String itemKey, ProcessItemHandler<?> processItemHandler) throws BpmExecutorException {
		
		if (processItemHandler == null) {
			throw new BpmExecutorException("cannot set a [NULL] custom handler!!", null, null);
		}
		customProcessItemHandlers.put(itemKey, processItemHandler);
		return this;
	}
	
	public BpmnDefinition withBpmStateChecker(String itemKey, BpmStateChecker bpmStateChecker) throws BpmExecutorException {
		
		if (bpmStateChecker == null) {
			throw new BpmExecutorException("cannot set a [NULL] state checker!!", null, null);
		}
		bpmStateCheckers.put(itemKey, bpmStateChecker);
		return this;
	}
	
	public BpmnDefinition withSettings(ProcessExecutorSettings aProcessExecutorSettings) {
		this.processExecutorSettings = aProcessExecutorSettings;
		return this;
	}
	
	public String toString() {
		return processDefinitionKey;
	}

	public BpmnDefinition withStartHandler(IProcessStartHandler aProcessStartHandler) {
		this.processStartHandler = aProcessStartHandler;
		return this;
	}
}