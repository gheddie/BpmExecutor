package de.gravitex.bpm.executor.delegate;

import java.util.HashMap;

public class CheckAdressLogicSingleton {

	private static CheckAdressLogicSingleton instance;
	
	private HashMap<String, CheckAddressObject> checkAddressObjects = new HashMap<String, CheckAddressObject>();
	
	private CheckAdressLogicSingleton() {
		super();
	}

	public static CheckAdressLogicSingleton getInstance() {
		if (instance == null) {
			instance = new CheckAdressLogicSingleton();
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