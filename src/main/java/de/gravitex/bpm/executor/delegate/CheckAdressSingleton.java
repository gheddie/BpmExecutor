package de.gravitex.bpm.executor.delegate;

import java.util.HashMap;

public class CheckAdressSingleton {

	private static CheckAdressSingleton instance;
	
	private HashMap<String, CheckAddressObject> checkAddressObjects = new HashMap<String, CheckAddressObject>();
	
	private CheckAdressSingleton() {
		super();
	}

	public static CheckAdressSingleton getInstance() {
		if (instance == null) {
			instance = new CheckAdressSingleton();
		}
		return instance;
	}

	public int counter(String processInstanceId) {
		if (checkAddressObjects.get(processInstanceId) == null) {
			checkAddressObjects.put(processInstanceId, new CheckAddressObject());
		}
		return checkAddressObjects.get(processInstanceId).counter();
	}
}