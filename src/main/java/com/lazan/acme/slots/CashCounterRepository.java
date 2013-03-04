package com.lazan.acme.slots;

import java.util.Date;


public interface CashCounterRepository {
	public void inputAudit(String message);
	public void persistBag(Bag bag);
	public Bag getBag(String bagId);
	public Iterable<Bag> getUnmatchedBagsBefore(Date date);
}
