package de.gravitex.bpm.executor.util;

import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class ProcessItemFormatter {

	public static String getKey(Object itemToExecute) throws BpmExecutorException {
		if (itemToExecute instanceof TaskEntity) {
			return formatKey("TASK", ((TaskEntity) itemToExecute).getName());
		} else if (itemToExecute instanceof TimerEntity) {
			return formatKey("TIMER", ((TimerEntity) itemToExecute).getJobHandlerConfigurationRaw());
		} else if (itemToExecute instanceof MessageEntity) {
			return formatKey("MESSAGE", ((MessageEntity) itemToExecute).getJobHandlerConfigurationRaw());
		} else if (itemToExecute instanceof EventSubscriptionEntity) {
			return formatKey("SUBSCRIPTION", ((EventSubscriptionEntity) itemToExecute).getEventName());
		}
		throw new BpmExecutorException("unable to create item key for type'" + itemToExecute.getClass().getCanonicalName() + "'!!", null,
				null);
	}

	private static String formatKey(String s1, String s2) {
		return s1 + "#" + s2;
	}
}