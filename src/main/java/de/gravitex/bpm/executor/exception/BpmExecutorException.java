package de.gravitex.bpm.executor.exception;

public class BpmExecutorException extends Exception {
	
	private static final long serialVersionUID = 1818842352957962337L;

	public BpmExecutorException(String message, Throwable t) {
		super(message, t);
	}
}