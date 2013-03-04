package com.lazan.acme.slots.internal;

import java.util.Date;

import com.lazan.acme.slots.Bag;
import com.lazan.acme.slots.CashCounterOutputListener;
import com.lazan.acme.slots.CashCounterRepository;

/**
 * Asynchronous task to alert the UI of unmatched bags
 */
public class UnmatchedBagWorker implements Runnable {
	private final long maxUnmatchedPeriod;
	private final CashCounterRepository repository;
	private final CashCounterOutputListener outputListener;
	
	public UnmatchedBagWorker(long maxUnmatchedPeriod, CashCounterRepository repository, CashCounterOutputListener outputListener) {
		super();
		this.maxUnmatchedPeriod = maxUnmatchedPeriod;
		this.repository = repository;
		this.outputListener = outputListener;
	}

	@Override
	public void run() {
		Date minDate = new Date(System.currentTimeMillis() - maxUnmatchedPeriod);
		for (Bag bag : repository.findUnmatchedBagsBefore(minDate)) {
			outputListener.bagNotMatched(bag.getBagId(), bag.getTotal());
		}
	}
}
