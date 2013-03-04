package com.lazan.acme.slots;

import java.util.Date;


public interface CashCounterRepository {
	/**
	 * Audit an input message
	 * @param message 
	 */
	public void inputAudit(String message);
	
	/**
	 * Insert or update the bag in the repository
	 * @param bag
	 */
	public void persistBag(Bag bag);
	
	/**
	 * Retrieve a bag previously persisted to the repository
	 * @param bagId
	 * @return
	 */
	public Bag getBag(String bagId);
	
	/**
	 * Find bags which have not been matched that were created before the date provided
	 * @param date Date filter
	 * @return Collection of bags
	 */
	public Iterable<Bag> findUnmatchedBagsBefore(Date date);
}
