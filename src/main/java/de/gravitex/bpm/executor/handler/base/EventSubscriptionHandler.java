package de.gravitex.bpm.executor.handler.base;

import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.runtime.EventSubscription;

public class EventSubscriptionHandler extends ProcessItemHandler<EventSubscription> {
	
	private static final Logger logger = Logger.getLogger(EventSubscriptionHandler.class);

	@Override
	protected EventSubscription castProcessItem(Object processItem) {
		return (EventSubscription) processItem;
	}

	@Override
	public final void handleLifeCycleBegin(Object processItem) {
		logger.info("handling: " + castProcessItem(processItem).getEventName());
		correlateMessage(processItem, null);
	}

	public void correlateMessage(Object processItem, Map<String, Object> variables) {
		runtimeService().correlateMessage(castProcessItem(processItem).getEventName());
	}

	@Override
	public final void handleLifeCycle(Object processItem) {
		// TODO Auto-generated method stub
	}

	@Override
	public final void handleLifeCycleEnd(Object processItem) {
		// TODO Auto-generated method stub
	}
}