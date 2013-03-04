package com.lazan.acme.slots.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.lazan.acme.slots.Bag;
import com.lazan.acme.slots.BagState;
import com.lazan.acme.slots.CashCounterOutputListener;
import com.lazan.acme.slots.CashCounterRepository;
import com.lazan.acme.slots.DenominationType;
import com.lazan.acme.slots.InputEvent;
import com.lmax.disruptor.EventHandler;

public class BagTotalEventHandler implements EventHandler<InputEvent> {
	private final CashCounterRepository repository;
	private final CashCounterOutputListener outputListener;
	private Map<String, Bag> tempBags = new HashMap<String, Bag>();
	
	private int noteTotal = 0;
	private int coinTotal = 0;
	
	public BagTotalEventHandler(CashCounterRepository repository, CashCounterOutputListener outputListener) {
		super();
		this.repository = repository;
		this.outputListener = outputListener;
	}

	@Override
	public void onEvent(InputEvent event, long sequence, boolean endOfBatch) throws Exception {
		switch (event.getType()) {
			case BAG_ENTRY : onBagEntry(event); break;
			case BAG_END : onEndBag(event); break;
		}
	}

	protected void onEndBag(InputEvent event) {
		Bag tempBag = tempBags.remove(event.getBagId());
		if (tempBag != null) {
			tempBag.setCreated(new Date());
			repository.persistBag(tempBag);
		}
	}

	protected void onBagEntry(InputEvent event) {
		String bagId = event.getBagId();
		Bag tempBag = tempBags.get(bagId);
		if (tempBag == null) {
			tempBag = new Bag();
			tempBag.setBagId(bagId);
			tempBag.setTotal(0);
			tempBag.setType(event.getDenomination().getType());
			tempBag.setState(BagState.RECEIVED);
			tempBags.put(bagId,  tempBag);
		}
		tempBag.setTotal(tempBag.getTotal() + event.getDenomination().getCentValue());

		switch (event.getDenomination().getType()) {
			case NOTE : 
				noteTotal += event.getDenomination().getCentValue();
				outputListener.runningTotal(DenominationType.NOTE, noteTotal);
				break;
				
			case COIN :
				coinTotal += event.getDenomination().getCentValue();
				outputListener.runningTotal(DenominationType.COIN, coinTotal);
				break;
		}
	}
}
