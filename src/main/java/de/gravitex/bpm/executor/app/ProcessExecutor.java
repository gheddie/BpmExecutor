package de.gravitex.bpm.executor.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.checker.base.BpmStateChecker;
import de.gravitex.bpm.executor.enumeration.ProcessExecutorState;
import de.gravitex.bpm.executor.handler.base.ProcessItemHandler;
import lombok.Data;

@Data
public class ProcessExecutor {
	
	private Date startDate;

	private ProcessInstance processInstance;
	
	private String businessKey;

	private BpmnDefinition bpmnDefinition;
	
	private ProcessExecutorState processExecutorState;
	
	private String activity;

	private List<String> pathElements = new ArrayList<String>();

	public ProcessExecutor (BpmnDefinition aBpmDefinition) {
		super();
		this.bpmnDefinition = aBpmDefinition;
	}

	public ProcessItemHandler<?> getHandler(String itemKey) {
		return bpmnDefinition.getCustomProcessItemHandlers().get(itemKey);
	}

	public BpmStateChecker getChecker(String itemKey) {
		return bpmnDefinition.getBpmStateCheckers().get(itemKey);
	}
	
	public String toString() {
		return bpmnDefinition.getProcessDefinitionKey() + " [ID=" + processInstance.getId() + "]";
	}

	public void addPathElement(String pathElement) {
		pathElements.add(pathElement);
	}
}