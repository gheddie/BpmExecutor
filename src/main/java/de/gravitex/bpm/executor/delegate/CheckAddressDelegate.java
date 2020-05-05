package de.gravitex.bpm.executor.delegate;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;

public class CheckAddressDelegate implements JavaDelegate {
	
	private static final Logger logger = Logger.getLogger(CheckAddressDelegate.class);

	private static final String ERROR_CHECK_ADDRESS = "ERROR_CHECK_ADDRESS";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		int counter = CheckAdressLogicSingleton.getInstance().counter(execution.getProcessInstance().getId());
		String msg1 = "counter ist at " + counter + "...";
		logger.info(msg1);
		putMessage(msg1, execution, null);
		if (counter < 3) {
			String msg2 = "something unexpected happens...";
			logger.info(msg2);
			putMessage(msg2, execution, null);
			throwSomethingUnexpected();
		} else {
			String msg3 = "rerouting to manual processing...";
			logger.info(msg3);
			putMessage(msg3, execution, null);
			throw new BpmnError(ERROR_CHECK_ADDRESS);
		}
	}

	private void putMessage(String message, DelegateExecution execution, Throwable t) {
		BpmExecutionSingleton.getInstance().putMessage(execution.getProcessInstance().getId(), message, t);
	}

	private void throwSomethingUnexpected() {
		throw new NullPointerException();
	}
}