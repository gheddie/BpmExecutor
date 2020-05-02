package de.gravitex.bpm.executor.app;

import java.util.HashMap;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.listener.IProcessProgressListener;
import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import de.gravitex.bpm.executor.settings.ProcessExecutorSettings;
import lombok.Data;

@Data
public class ProcessExecutor implements IProcessProgressListener {
	
	private ProcessInstance processInstance;
	
	private String businessKey;
	
	private ProcessExecutorSettings processExecutorSettings;
	
	private HashMap<String, ProcessItemHandler<?>> customProcessItemHandlers = new HashMap<String, ProcessItemHandler<?>>();
	
	private HashMap<String, BpmStateChecker> bpmStateCheckers = new HashMap<String, BpmStateChecker>();
	
	private String bpmFileName;
	
	private String processDefinitionKey;
	
	private ProcessExecutorState processExecutorState;

	private ProcessExecutor() {
		super();
	}

	public ProcessExecutor withCustomHandler(String itemKey, ProcessItemHandler<?> processItemHandler) throws BpmExecutorException {
		
		if (processItemHandler == null) {
			throw new BpmExecutorException("cannot set a [NULL] custom handler!!", null, null);
		}
		customProcessItemHandlers.put(itemKey, processItemHandler);
		return this;
	}
	
	public ProcessExecutor withBpmStateChecker(String itemKey, BpmStateChecker bpmStateChecker) throws BpmExecutorException {
		
		if (bpmStateChecker == null) {
			throw new BpmExecutorException("cannot set a [NULL] state checker!!", null, null);
		}
		bpmStateCheckers.put(itemKey, bpmStateChecker);
		return this;
	}
	
	public ProcessExecutor withSettings(ProcessExecutorSettings aProcessExecutorSettings) {
		this.processExecutorSettings = aProcessExecutorSettings;
		return this;
	}

	public static ProcessExecutor create(String aBpmFileName, String aProcessDefinitionKey) {
		
		ProcessExecutor processExecutor = new ProcessExecutor();
		processExecutor.setBpmFileName(aBpmFileName);
		processExecutor.setProcessDefinitionKey(aProcessDefinitionKey);
		return processExecutor;
	}

	public ProcessItemHandler<?> getHandler(String itemKey) {
		return customProcessItemHandlers.get(itemKey);
	}

	public BpmStateChecker getChecker(String itemKey) {
		return bpmStateCheckers.get(itemKey);
	}
}