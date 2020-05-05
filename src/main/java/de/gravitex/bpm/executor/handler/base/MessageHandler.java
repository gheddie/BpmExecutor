package de.gravitex.bpm.executor.handler.base;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.exception.BpmExecutorException;

public class MessageHandler extends ProcessItemHandler<MessageEntity> {
	
	private static final Logger logger = Logger.getLogger(MessageHandler.class);

	@Override
	public void handleLifeCycleBegin(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		logger.info("MessageHandler --> handleLifeCycleBegin...");
		/*
		MessageEntity messageEntity = castProcessItem(processItem);
		putMessage(processInstance,
				"trying execute message '" + messageEntity.getJobHandlerConfigurationRaw() + "' [" + messageEntity.getRetries() + " retries already]...",
				null);
		managementService().executeJob(messageEntity.getId());
		putMessage(processInstance,
				"succesfully executed message '" + messageEntity.getJobHandlerConfigurationRaw() + "'...", null);
				*/
	}

	@Override
	public void handleLifeCycle(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		logger.info("MessageHandler --> handleLifeCycle...");
		MessageEntity messageEntity = castProcessItem(processItem);
		putMessage(processInstance, "expiration: " + messageEntity.getLockExpirationTime() + ", retries: " + messageEntity.getRetries(), null);
		// putMessage(processInstance, "moo", null);
	}

	@Override
	public void handleLifeCycleEnd(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		logger.info("MessageHandler --> handleLifeCycleEnd...");
	}
	
	@Override
	protected MessageEntity castProcessItem(Object processItem) {
		return (MessageEntity) processItem;
	}
}