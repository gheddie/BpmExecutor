package de.gravitex.bpm.executor.handler.base;

import lombok.Data;

@Data
public class ProcessItemKey {

	private String itemKey;
	
	private String processDefinitionKey;
	
	private ProcessItemKey() {
		super();
	}
	
	public static ProcessItemKey fromValues(String anItemKey, String aProcessDefinitionKey) {
		ProcessItemKey processItemKey = new ProcessItemKey();
		processItemKey.setItemKey(anItemKey);
		processItemKey.setProcessDefinitionKey(format(aProcessDefinitionKey));
		return processItemKey;
	}

	private static String format(String aProcessDefinitionKey) {
		// TODO camunda only gives us something like 'ProcessXYZ:1:3', so...
		if (aProcessDefinitionKey.contains(":")) {
			String result = aProcessDefinitionKey.substring(0, aProcessDefinitionKey.indexOf(":"));
			return result;	
		}
		return aProcessDefinitionKey;
	}
}