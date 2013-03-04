package com.lazan.acme.slots;

public enum BagState {
	RECEIVED(false, false), 
	MATCHED(true, false), 
	VOLT_ASSIGNED(true, true);
	
	private boolean matched;
	private boolean voltAssigned;
	
	private BagState(boolean matched, boolean voltAssigned) {
		this.matched = matched;
		this.voltAssigned = voltAssigned;
	}
	
	public boolean isMatched() {
		return matched;
	}
	
	public boolean isVoltAssigned() {
		return voltAssigned;
	}
}
