package de.gravitex.bpm.executor.settings;

import lombok.Data;

@Data
public class ProcessExecutorSettings {

	private int allowedTimerDivergenceInSeconds;

	private boolean fireTimersImmediately;

	private boolean traceIntermediateLifeCycles;
	
	private ProcessExecutorSettings() {
		super();
	}

	public static ProcessExecutorSettings fromValues(int anAllowedTimerDivergenceInSeconds, boolean aFireTimersImmediately,
			boolean aTraceIntermediateLifeCycles) {

		ProcessExecutorSettings processExecutorSettings = new ProcessExecutorSettings();
		processExecutorSettings.setAllowedTimerDivergenceInSeconds(anAllowedTimerDivergenceInSeconds);
		processExecutorSettings.setFireTimersImmediately(aFireTimersImmediately);
		processExecutorSettings.setTraceIntermediateLifeCycles(aTraceIntermediateLifeCycles);
		return processExecutorSettings;
	}
}