package com.lazan.acme.slots;


public interface CashCounterOutputListener {
	public void runningTotal(DenominationType denominationType, int total);
	public void bagNotMatched(String bagId, int bagTotal);
	public void voltAssigned(String bagId, String voltId);
}
