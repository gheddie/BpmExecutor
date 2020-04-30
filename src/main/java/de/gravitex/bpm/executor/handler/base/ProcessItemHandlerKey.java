package de.gravitex.bpm.executor.handler.base;

import lombok.Data;

@Data
public class ProcessItemHandlerKey {

	private String itemKey;
	
	private String processDefinitionKey;
	
	private ProcessItemHandlerKey() {
		super();
	}
	
	public static ProcessItemHandlerKey fromValues(String anItemKey, String aProcessDefinitionKey) {
		ProcessItemHandlerKey processItemHandlerKey = new ProcessItemHandlerKey();
		processItemHandlerKey.setItemKey(anItemKey);
		processItemHandlerKey.setProcessDefinitionKey(format(aProcessDefinitionKey));
		return processItemHandlerKey;
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