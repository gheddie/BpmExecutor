package de.gravitex.bpm.executor.delegate;

import lombok.Data;

@Data
public class CheckAddressObject {

	private int counter = 0;

	public int counter() {
		return counter++;
	}
}