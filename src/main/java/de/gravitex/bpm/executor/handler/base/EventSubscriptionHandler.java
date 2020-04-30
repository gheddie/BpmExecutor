package de.gravitex.bpm.executor.handler.base;

import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class EventSubscriptionHandler extends ProcessItemHandler<EventSubscription> {
	
	private static final Logger logger = Logger.getLogger(EventSubscriptionHandler.class);

	@Override
	protected EventSubscription castProcessItem(Object processItem) {
		return (EventSubscription) processItem;
	}

	@Override
	public final void handleLifeCycleBegin(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		logger.info(formatForProcessInstance("handling: " + castProcessItem(processItem).getEventName(), processInstance));
		correlateMessage(processItem, BpmExecutionSingleton.getInstance().getBusinessKey(processInstance), null);
		invokeProcessStateChecker(castProcessItem(processItem), processInstance);
	}

	public void correlateMessage(Object processItem, String businessKey, Map<String, Object> variables) {
		runtimeService().correlateMessage(castProcessItem(processItem).getEventName(), businessKey, variables);
	}

	@Override
	public final void handleLifeCycle(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		// TODO Auto-generated method stub
	}

	@Override
	public final void handleLifeCycleEnd(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		// TODO Auto-generated method stub
	}
}