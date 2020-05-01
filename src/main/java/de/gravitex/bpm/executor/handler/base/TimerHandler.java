package de.gravitex.bpm.executor.handler.base;

import java.util.Date;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.bpm.executor.app.BpmExecutionSingleton;
import de.gravitex.bpm.executor.enumeration.ExecutionPhase;
import de.gravitex.bpm.executor.exception.BpmExecutorException;
import de.gravitex.bpm.executor.util.ProcessUtil;

public class TimerHandler extends ProcessItemHandler<TimerEntity> {

	private static final Logger logger = Logger.getLogger(TimerHandler.class);

	@Override
	public final void handleLifeCycleBegin(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		
		if (BpmExecutionSingleton.getInstance().getProcessExecutorSettings().isFireTimersImmediately()) {
			TimerEntity timer = castProcessItem(processItem);
			invokeProcessStateChecker(timer, processInstance, ExecutionPhase.BEFORE_PROCESSING);
			managementService().executeJob(timer.getId());
			logger.info("fired timer '" + timer.getJobHandlerConfigurationRaw() + "' immediately (for given settings)...");
			invokeProcessStateChecker(timer, processInstance, ExecutionPhase.AFTER_PROCESSING);
		}
	}

	@Override
	public final void handleLifeCycle(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		TimerEntity timer = castProcessItem(processItem);
		long seconds = ProcessUtil.getDateDiffInSeconds(timer.getDuedate(), new Date());
		if (BpmExecutionSingleton.getInstance().getProcessExecutorSettings().isTraceIntermediateLifeCycles()) {
			logger.info(formatForProcessInstance("handling timer '" + timer.getJobHandlerConfigurationRaw() + "' life cycle --> " + seconds + " seconds to go... ", processInstance));			
		}
	}

	@Override
	public final void handleLifeCycleEnd(Object processItem, ProcessInstance processInstance) throws BpmExecutorException {
		// TODO how to handle exection phase 'BEFORE_RPOCESSING'?
		if (BpmExecutionSingleton.getInstance().getProcessExecutorSettings().isFireTimersImmediately()) {
			return;
		}
		TimerEntity timer = castProcessItem(processItem);
		long dateDiffInSecondsFromTarget = Math.abs(ProcessUtil.getDateDiffInSeconds(timer.getDuedate(), new Date()));
		int allowedTimerDivergenceInSeconds = Math
				.abs(BpmExecutionSingleton.getInstance().getProcessExecutorSettings().getAllowedTimerDivergenceInSeconds());
		if (allowedTimerDivergenceInSeconds > 0 && dateDiffInSecondsFromTarget > allowedTimerDivergenceInSeconds) {
			throw new BpmExecutorException(
					"timer '" + timer.getJobHandlerConfigurationRaw() + "' fired inaccurate (allowed="
							+ allowedTimerDivergenceInSeconds + ", actual=" + dateDiffInSecondsFromTarget + " seconds)!!",
					null);
		}
		logger.info("timer " + timer.getJobHandlerConfigurationRaw() + " has reached due date ("
				+ dateDiffInSecondsFromTarget + " seconds from target date).");
		invokeProcessStateChecker(timer, processInstance, ExecutionPhase.AFTER_PROCESSING);
	}

	@Override
	protected TimerEntity castProcessItem(Object processItem) {
		return (TimerEntity) processItem;
	}
}