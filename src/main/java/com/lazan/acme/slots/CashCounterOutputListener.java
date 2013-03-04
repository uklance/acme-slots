package com.lazan.acme.slots;


public interface CashCounterOutputListener {
	public void runningTotal(DenominationType denominationType, Integer runningTotalCents);
	public void bagNotMatched(String bagId, Integer bagTotalCents);
	public void bagVolt(String bagId, String voltId);
}
