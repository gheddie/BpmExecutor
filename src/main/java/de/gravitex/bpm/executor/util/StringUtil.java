package de.gravitex.bpm.executor.util;

import java.util.List;

public class StringUtil {

	public static String formatList(List<String> aStrings) {
		if (aStrings == null || aStrings.size() == 0) {
			return "[]";
		}
		String result = "[";
		int index = 0;
		for (String string : aStrings) {
			result += string;
			if (index < aStrings.size() - 1) {
				result += ",";
			}
			index++;
		}
		return result + "]";
	}
}