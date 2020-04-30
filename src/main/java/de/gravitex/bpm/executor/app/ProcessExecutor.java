package de.gravitex.bpm.executor.app;

import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import lombok.Data;

@Data
public class ProcessExecutor {
	
	private ProcessExecutor() {
		super();
	}

	public ProcessExecutor withCustomHandler(String processDefinitionKey, String itemKey, ProcessItemHandler<?> processItemHandler) throws BpmExecutorException {
		
		if (processItemHandler == null) {
			throw new BpmExecutorException("cannot set a [NULL] custom handler!!", null);
		}
		BpmExecutionSingleton.getInstance().registerHandler(processDefinitionKey, itemKey, processItemHandler);
		return this;
	}
	
	public ProcessExecutor withBpmStateChecker(String processDefinitionKey, String itemKey, BpmStateChecker bpmStateChecker) throws BpmExecutorException {
		
		if (bpmStateChecker == null) {
			throw new BpmExecutorException("cannot set a [NULL] state checker!!", null);
		}
		BpmExecutionSingleton.getInstance().registerChecker(processDefinitionKey, itemKey, bpmStateChecker);
		return this;
	}

	public void startProcess(String processDefinitionKey, int count) {
		BpmExecutionSingleton.getInstance().startProcessInstance(processDefinitionKey, count);
	}

	public ProcessExecutor withSettings(ProcessExecutorSettings aProcessExecutorSettings) {
		BpmExecutionSingleton.getInstance().setProcessExecutorSettings(aProcessExecutorSettings);
		return this;
	}

	public ProcessExecutor addDeployment(String bpmnFileName) {
		BpmExecutionSingleton.getInstance().deployProcess(bpmnFileName);
		return this;
	}

	public static ProcessExecutor create() {
		return new ProcessExecutor();
	}
}