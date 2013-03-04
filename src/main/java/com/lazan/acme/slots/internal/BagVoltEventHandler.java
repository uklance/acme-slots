package com.lazan.acme.slots.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.lazan.acme.slots.Bag;
import com.lazan.acme.slots.BagState;
import com.lazan.acme.slots.CashCounterOutputListener;
import com.lazan.acme.slots.CashCounterRepository;
import com.lazan.acme.slots.InputEvent;
import com.lazan.acme.slots.InputEventType;
import com.lazan.acme.slots.VoltService;
import com.lmax.disruptor.EventHandler;

public class BagVoltEventHandler implements EventHandler<InputEvent> {
	private final CashCounterRepository repository;
	private final VoltService voltService;
	private final CashCounterOutputListener outputListener;
	
	private Set<Bag> unmatchedBags = new LinkedHashSet<Bag>();
	
	public BagVoltEventHandler(CashCounterRepository repository, VoltService voltService, CashCounterOutputListener outputListener) {
		super();
		this.repository = repository;
		this.voltService = voltService;
		this.outputListener = outputListener;
	}

	/**
	 * Note this currently has a complexity of N^2 which is terrible
	 */
	@Override
	public void onEvent(InputEvent event, long sequence, boolean endOfBatch) throws Exception {
		if (event.getType() == InputEventType.END_BAG) {
			Bag bag = repository.getBag(event.getBagId());
			unmatchedBags.add(bag);
			
			for (Bag current : unmatchedBags) {
				Collection<Bag> matches = findMatches(current);
				if (matches != null) {
					unmatchedBags.remove(current);
					unmatchedBags.removeAll(matches);
					assignVolt(current, matches);
				}
			}
		}
	}

	protected void assignVolt(Bag bag, Collection<Bag> matches) {
		List<Bag> voltGroup = new ArrayList<Bag>(matches);
		voltGroup.add(bag);
		
		// update as matched
		for (Bag updateMe : voltGroup) {
			updateMe.setState(BagState.MATCHED);
			repository.persistBag(updateMe);
		}
		
		// this webservice could take a while
		String voltId = voltService.getVoltId(bag, matches);

		// update as volt assigned
		for (Bag updateMe : voltGroup) {
			updateMe.setState(BagState.VOLT_ASSIGNED);
			updateMe.setVoltId(voltId);
			repository.persistBag(updateMe);
			outputListener.voltAssigned(updateMe.getBagId(), voltId);
		}
	}

	protected Collection<Bag> findMatches(Bag bag) {
		List<Bag> matches = new ArrayList<Bag>();
		int remainingTotal = bag.getTotal();
		for (Bag current : unmatchedBags) {
			if (current.getType() != bag.getType()) {
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
		}
		return null;
	}
}
