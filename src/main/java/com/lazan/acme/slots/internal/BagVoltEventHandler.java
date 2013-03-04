package com.lazan.acme.slots.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.lazan.acme.slots.Bag;
import com.lazan.acme.slots.BagState;
import com.lazan.acme.slots.CashCounterRepository;
import com.lazan.acme.slots.InputEvent;
import com.lazan.acme.slots.InputEventType;
import com.lazan.acme.slots.VoltService;
import com.lmax.disruptor.EventHandler;

public class BagVoltEventHandler implements EventHandler<InputEvent> {
	private final CashCounterRepository repository;
	private final VoltService voltService;
	
	private Set<Bag> unmatchedBags = new LinkedHashSet<Bag>();
	
	public BagVoltEventHandler(CashCounterRepository repository, VoltService voltService) {
		super();
		this.repository = repository;
		this.voltService = voltService;
	}

	@Override
	public void onEvent(InputEvent event, long sequence, boolean endOfBatch) throws Exception {
		if (event.getType() == InputEventType.END_BAG) {
			Bag bag = repository.getBag(event.getBagId());
			
			Collection<Bag> matches = findMatches(bag);
			if (matches == null) {
				unmatchedBags.add(bag);
			} else {
				List<Bag> toBeUpdated = new ArrayList<Bag>(matches);
				toBeUpdated.add(bag);
				
				// update as matched
				for (Bag updateMe : toBeUpdated) {
					updateMe.setState(BagState.MATCHED);
					repository.persistBag(updateMe);
				}
				
				// this webservice could take a while
				String voltId = voltService.getVoltId(bag, matches);

				// update as volt assigned
				for (Bag updateMe : toBeUpdated) {
					updateMe.setState(BagState.VOLT_ASSIGNED);
					updateMe.setVoltId(voltId);
					repository.persistBag(updateMe);
				}
			}
		}
	}

	protected Collection<Bag> findMatches(Bag bag) {
		List<Bag> matches = new ArrayList<Bag>();
		int remainingTotal = bag.getTotal();
		for (Bag current : unmatchedBags) {
			if (current.getTotal() == bag.getTotal()) {
				return Collections.singleton(current);
			}
			if (current.getTotal() <= remainingTotal) {
				matches.add(current);
				remainingTotal -= current.getTotal();
				if (remainingTotal == 0) {
					return matches;
				}
			}
		}
		return null;
	}
}
