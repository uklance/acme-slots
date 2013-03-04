package com.lazan.acme.slots.internal;

import com.lazan.acme.slots.CashCounterInputListener;
import com.lazan.acme.slots.CashCounterRepository;
import com.lazan.acme.slots.Denomination;
import com.lazan.acme.slots.InputEvent;
import com.lazan.acme.slots.InputEventType;
import com.lmax.disruptor.RingBuffer;

public class DisruptorCashCounterInputListener implements CashCounterInputListener {
	private final RingBuffer<InputEvent> ringBuffer;
	private final CashCounterRepository repository;
	
	public DisruptorCashCounterInputListener(RingBuffer<InputEvent> ringBuffer, CashCounterRepository repository) {
		super();
		this.ringBuffer = ringBuffer;
		this.repository = repository;
	}

	@Override
	public void startBag(String bagId) {
		repository.inputAudit("START " + bagId);
		publish(InputEventType.START_BAG, bagId, null);
	}

	@Override
	public void bagEntry(String bagId, Denomination denomination) {
		repository.inputAudit(String.format("ENTRY %s %s", bagId, denomination.name()));
		publish(InputEventType.BAG_ENTRY, bagId, denomination);
	}
	
	@Override
	public void endBag(String bagId) {
		repository.inputAudit("END " + bagId);
		publish(InputEventType.END_BAG, bagId, null);
	}
	
	protected void publish(InputEventType type, String bagId, Denomination denomination) {
		long sequence = ringBuffer.next();
		InputEvent event = ringBuffer.get(sequence);
		event.set(type, bagId, denomination);
		ringBuffer.publish(sequence);
	}
}
