package com.lazan.acme.slots;

import java.math.BigDecimal;

public interface CashCounterOutputListener {
	public void runningTotal(BagType bagType, BigDecimal runningTotal);
	public void bagNotMatched(String bagId, BigDecimal bagTotal);
	public void bagVolt(String bagId, String voltId);
}
