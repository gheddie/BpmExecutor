package de.gravitex.bpm.executor.settings;

import lombok.Data;

@Data
public class ProcessExecutorSettings {

	private int allowedTimerDivergenceInSeconds;

	private boolean fireTimersImmediately;

	private boolean traceIntermediateLifeCycles;
	
	private int stepMillis;

	private ProcessExecutorSettings() {
		super();
	}

	public static ProcessExecutorSettings fromValues(int anAllowedTimerDivergenceInSeconds, boolean aFireTimersImmediately,
			boolean aTraceIntermediateLifeCycles, int aStepMillis) {

		ProcessExecutorSettings processExecutorSettings = new ProcessExecutorSettings();
		processExecutorSettings.setAllowedTimerDivergenceInSeconds(anAllowedTimerDivergenceInSeconds);
		processExecutorSettings.setFireTimersImmediately(aFireTimersImmediately);
		processExecutorSettings.setTraceIntermediateLifeCycles(aTraceIntermediateLifeCycles);
		processExecutorSettings.setStepMillis(aStepMillis);
		return processExecutorSettings;
	}
}