package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import lombok.Data;

@Data
public class ProcessExecutor {
	
	private String bpmnFileName;
	
	private String processDefinitionKey;

	private ProcessExecutor() {
		super();
	}

	public static ProcessExecutor fromValues(String aBpmnFileName, String aProcessDefinitionKey) throws BpmExecutorException {
		
		if (aBpmnFileName == null || aBpmnFileName.length() == 0) {
			throw new BpmExecutorException("bpmn file must be set!!", null);
		}
		if (aProcessDefinitionKey == null || aProcessDefinitionKey.length() == 0) {
			throw new BpmExecutorException("process definition key must be set!!", null);
		}
		ProcessExecutor processExecutor = new ProcessExecutor();
		processExecutor.setBpmnFileName(aBpmnFileName);
		processExecutor.setProcessDefinitionKey(aProcessDefinitionKey);
		BpmExecutionSingleton.getInstance().deployProcess(aBpmnFileName);
		return processExecutor;
	}

	public ProcessExecutor withCustomHandler(String key, ProcessItemHandler<?> processItemHandler) throws BpmExecutorException {
		
		if (processItemHandler == null) {
			throw new BpmExecutorException("cannot set a [NULL] custom handler!!", null);
		}
		BpmExecutionSingleton.getInstance().registerHandler(key, processItemHandler);
		return this;
	}

	public void startProcess() {
		BpmExecutionSingleton.getInstance().startProcessInstance(processDefinitionKey);
	}

	public ProcessExecutor withSettings(ProcessExecutorSettings aProcessExecutorSettings) {
		BpmExecutionSingleton.getInstance().setProcessExecutorSettings(aProcessExecutorSettings);
		return this;
	}
}