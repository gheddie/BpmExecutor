package de.gravitex.bpm.executor.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CheckAddressDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		int counter = CheckAdressSingleton.getInstance().counter(execution.getProcessInstance().getId());
		System.out.println("counter: " + counter);
		if (counter < 4) {
			throw new NullPointerException();
		}
	}
}