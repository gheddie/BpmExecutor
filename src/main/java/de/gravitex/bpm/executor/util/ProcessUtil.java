package de.gravitex.bpm.executor.util;

import java.util.Date;

public class ProcessUtil {

	public static long getDateDiffInSeconds(Date date1, Date date2) {
		
		long diffInMillies = date1.getTime() - date2.getTime();
		long seconds = diffInMillies / 1000;
		return seconds;
	}
}