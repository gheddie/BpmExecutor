package de.gravitex.bpm.executor.handler.base;

import java.util.Date;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.ProcessExecutor;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.util.ProcessUtil;

public class MessageHandler extends ProcessItemHandler<MessageEntity> {
	
	private static final Logger logger = Logger.getLogger(MessageHandler.class);

	@Override
	public void handleLifeCycleBegin(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException {
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
	public void handleLifeCycle(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException {
		logger.info("MessageHandler --> handleLifeCycle...");
		MessageEntity messageEntity = castProcessItem(processItem);
		putMessage(processExecutor.getProcessInstance(),
				"message expiration in " + ProcessUtil.getDateDiffInSeconds(messageEntity.getLockExpirationTime(), new Date())
						+ " seconds, retries: " + messageEntity.getRetries(),
				null);
	}

	@Override
	public void handleLifeCycleEnd(Object processItem, ProcessExecutor processExecutor) throws BpmExecutorException {
		logger.info("MessageHandler --> handleLifeCycleEnd...");
	}
	
	@Override
	protected MessageEntity castProcessItem(Object processItem) {
		return (MessageEntity) processItem;
	}
}