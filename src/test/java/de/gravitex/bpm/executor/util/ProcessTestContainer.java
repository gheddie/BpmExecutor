package de.gravitex.bpm.executor.util;

import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.app.BpmnDefinition;
import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.app.listener.IProcessExecutionListener;
import de.gravitex.bpm.executor.enumeration.ExecutionPhase;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import lombok.Data;

@Data
public class ProcessTestContainer implements IProcessExecutionListener {
	
	private boolean processFinished = false;
	
	private boolean processFailed = false;
	
	private BpmnDefinition bpmnDefinition;

	private ProcessExecutor processExecutor;

	public ProcessTestContainer(BpmnDefinition aBpmnDefinition) {
		super();
		this.bpmnDefinition = aBpmnDefinition;
	}

	@Override
	public void processFinished(ProcessExecutor aProcessExecutor) {
		processFinished = true;
		this.processExecutor = aProcessExecutor;
	}
	
	@Override
	public void processFailed(ProcessExecutor aProcessExecutor) {
		processFailed = true;
		this.processExecutor = aProcessExecutor;
	}

	public void startProcess() {
		try {
			BpmExecutionSingleton.getInstance().setProcessExecutionListener(this)
					.registerProcessDefinition(bpmnDefinition)
					.startProcess(bpmnDefinition.getProcessDefinitionKey());
		} catch (BpmExecutorException e) {
			e.printStackTrace();
		}
		while (run()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	private boolean run() {
		return (processFinished == false && processFailed == false);
	}

	public List<String> getProcessPath() {
		return processExecutor.getPathElements();
	}

	@Override
	public void itemFinalized(Object processItem, ProcessInstance processInstance, ExecutionPhase executionPhase) {
		int werner = 5;
	}
}