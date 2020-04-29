package de.gravitex.bpm.executor.settings;

import lombok.Data;

@Data
public class ProcessExecutorSettings {

	private int allowedTimerDivergenceInSeconds;

	private boolean fireTimersImmediately;

	private ProcessExecutorSettings() {
		super();
	}

	public static ProcessExecutorSettings fromValues(int anAllowedTimerDivergenceInSeconds, boolean aFireTimersImmediately) {

		ProcessExecutorSettings processExecutorSettings = new ProcessExecutorSettings();
		processExecutorSettings.setAllowedTimerDivergenceInSeconds(anAllowedTimerDivergenceInSeconds);
		processExecutorSettings.setFireTimersImmediately(aFireTimersImmediately);
		return processExecutorSettings;
	}
}