package de.gravitex.bpm.executor.util;

import java.util.HashMap;
import lombok.Data;

@Data
public class HashMapBuilder {
	
	private HashMap<String, Object> values = new HashMap<String, Object>();

	private HashMapBuilder() {
		super();
	}

	public static HashMapBuilder create() {
		return new HashMapBuilder();
	}

	public HashMapBuilder withValues(String key, Object value) {
		values.put(key, value);
		return this;
	}
	
	public HashMap<String, Object> result() {
		return values;
	}
}