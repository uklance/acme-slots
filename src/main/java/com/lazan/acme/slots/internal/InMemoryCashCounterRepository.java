package com.lazan.acme.slots.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.lazan.acme.slots.Bag;
import com.lazan.acme.slots.CashCounterRepository;

/**
 * Simple in-memory repository
 */
public class InMemoryCashCounterRepository implements CashCounterRepository {
	private Map<String, Bag> bags = new ConcurrentHashMap<String, Bag>();
	private AtomicInteger nextBagIncrement = new AtomicInteger(1);

	@Override
	public void inputAudit(String message) {
		System.out.println("AUDIT: " + message);
	}
	
	@Override
	public void persistBag(Bag bag) {
		//System.out.println(String.format("PERSIST %s %s %s %s %s", bag.getBagId(), bag.getType(), bag.getTotal(), bag.getState(), bag.getVoltId()));
		if (bag.getBagId() == null) {
			String bagId = "B" + nextBagIncrement.getAndIncrement();
			bag.setBagId(bagId);
		}
		bags.put(bag.getBagId(), bag);
	}
	
	@Override
	public Bag getBag(String bagId) {
		Bag bag = bags.get(bagId);
		if (bag == null) {
			throw new IllegalStateException("No such bag " + bagId);
		}
		return bag;
	}
	
	/**
	 * TODO: The complexity of this is currently O(n).
	 * Not too important since it's called offline in a scheduled task
	 */
	@Override
	public Iterable<Bag> findUnmatchedBagsBefore(Date date) {
		List<Bag> unmatched = new ArrayList<Bag>();
		for (Bag bag : bags.values()) {
			if (!bag.getState().isMatched() && bag.getCreated().before(date)) {
				unmatched.add(bag);
			}
		}
		return unmatched;
	}
}
