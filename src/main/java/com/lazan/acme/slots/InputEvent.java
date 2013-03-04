package com.lazan.acme.slots;

import com.lmax.disruptor.EventFactory;

public class InputEvent {
	private InputEventType type;
	private String bagId;
	private Denomination denomination;
	
	public static final EventFactory<InputEvent> EVENT_FACTORY = new EventFactory<InputEvent>() {
		@Override
		public InputEvent newInstance() {
			return new InputEvent();
		}
	};
	
	public InputEventType getType() {
		return type;
	}
	
	public String getBagId() {
		return bagId;
	}
	
	public Denomination getDenomination() {
		return denomination;
	}
	
	public void set(InputEventType type, String bagId, Denomination denomination) {
		this.type = type;
		this.bagId = bagId;
		this.denomination = denomination;
	}
}
