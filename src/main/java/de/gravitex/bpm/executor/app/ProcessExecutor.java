package de.gravitex.bpm.executor.app;

import java.util.Date;

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

	private BpmDefinition bpmDefinition;
	
	private ProcessExecutorState processExecutorState;
	
	private String activity;

	public ProcessExecutor (BpmDefinition aBpmDefinition) {
		super();
		this.bpmDefinition = aBpmDefinition;
	}

	public ProcessItemHandler<?> getHandler(String itemKey) {
		return bpmDefinition.getCustomProcessItemHandlers().get(itemKey);
	}

	public BpmStateChecker getChecker(String itemKey) {
		return bpmDefinition.getBpmStateCheckers().get(itemKey);
	}
	
	public String toString() {
		return bpmDefinition.getProcessDefinitionKey() + " [ID=" + processInstance.getId() + "]";
	}
}