package de.gravitex.bpm.executor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.bpm.executor.enumeration.LifeCycle;
import lombok.Data;

@Data
public class DiffContainer {

	private HashMap<LifeCycle, List<Object>> items = new HashMap<LifeCycle, List<Object>>();
	
	public DiffContainer addItem(Object object, LifeCycle lifeCycle) {
		if (items.get(lifeCycle) == null) {
			items.put(lifeCycle, new ArrayList<Object>());
		} 
		items.get(lifeCycle).add(object);
		return this;
	}

	public int getChangeCount() {
		
		int changeCount = 0;
		for (LifeCycle lifeCycle : items.keySet()) {
			changeCount += items.get(lifeCycle).size();
		}
		return changeCount;
	}
}