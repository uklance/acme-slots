package com.lazan.acme.slots;

/**
 * Input API for the Cash Counter
 */
public interface CashCounterInputListener {
	public void startBag(String bagId);
	public void bagEntry(String bagId, Denomination denomination);
	public void endBag(String bagId);
}
