package de.gravitex.bpm.executor.delegate;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class T4Delegate implements JavaDelegate {
	
	private static final Logger logger = Logger.getLogger(T4Delegate.class);

	public void execute(DelegateExecution execution) throws Exception {
		logger.info(" --- T1Delegate --- ");
	}
}