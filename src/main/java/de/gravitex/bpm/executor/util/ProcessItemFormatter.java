package de.gravitex.bpm.executor.util;

import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.task.Task;

public class ProcessItemFormatter {

	public static String getKey(Object itemToExecute) {
		if (itemToExecute instanceof Task) {
			return formatKey("TASK", ((Task) itemToExecute).getName());
		} else if (itemToExecute instanceof Job) {
			return formatKey("JOB", ((TimerEntity) itemToExecute).getJobHandlerConfigurationRaw());
		}
		return null;
	}

	private static String formatKey(String s1, String s2) {
		return s1 + "#" + s2;
	}
}