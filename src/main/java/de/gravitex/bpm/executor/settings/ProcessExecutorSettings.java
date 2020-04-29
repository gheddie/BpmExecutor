package de.gravitex.bpm.executor.settings;

import lombok.Data;

@Data
public class ProcessExecutorSettings {

	private int allowedTimerDivergenceInMinutes;

	private boolean fireTimersImmediately;

	private ProcessExecutorSettings() {
		super();
	}

	public static ProcessExecutorSettings fromValues(int anAllowedTimerDivergenceInMinutes, boolean aFireTimersImmediately) {

		ProcessExecutorSettings processExecutorSettings = new ProcessExecutorSettings();
		processExecutorSettings.setAllowedTimerDivergenceInMinutes(anAllowedTimerDivergenceInMinutes);
		processExecutorSettings.setFireTimersImmediately(aFireTimersImmediately);
		return processExecutorSettings;
	}
}