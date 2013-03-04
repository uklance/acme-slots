package com.lazan.acme.slots;

public interface CashCounterInputListener {
	public void startBag(String bagId);
	public void endBag(String bagId);
	public void bagEntry(String bagId, Denomination denom);
}
