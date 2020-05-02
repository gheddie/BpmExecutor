package de.gravitex.bpm.executor.exception;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import lombok.Data;

@Data
public class BpmExecutorException extends Exception {
	
	private static final long serialVersionUID = 1818842352957962337L;
	
	private ProcessInstance processInstance;

	public BpmExecutorException(String message, Throwable t, ProcessInstance aProcessInstance) {
		super(message, t);
		this.processInstance = aProcessInstance;
	}
}