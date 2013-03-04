package com.lazan.acme.slots;

/**
 * Output API for the Cash Counter
 */
public interface CashCounterOutputListener {
	public void runningTotal(DenominationType denominationType, int total);
	public void bagNotMatched(String bagId, int bagTotal);
	public void voltAssigned(String bagId, String voltId);
}
