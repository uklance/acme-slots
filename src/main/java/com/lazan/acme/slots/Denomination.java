package com.lazan.acme.slots;

public enum Denomination {
	NOTE_1(100, DenominationType.NOTE),
	NOTE_10(10 * 100, DenominationType.NOTE),
	NOTE_20(20 * 100, DenominationType.NOTE),
	NOTE_50(50 * 100, DenominationType.NOTE),
	COIN_1(1, DenominationType.COIN),
	COIN_5(5, DenominationType.COIN),
	COIN_10(10, DenominationType.COIN),
	COIN_20(20, DenominationType.COIN),
	COIN_50(50, DenominationType.COIN),
	COIN_100(100, DenominationType.COIN);
	
	private Denomination(int centValue, DenominationType type) {
		this.centValue = centValue;
		this.type = type;
	}

	private int centValue;
	private DenominationType type;
	
	public int getCentValue() {
		return centValue;
	}
	
	public DenominationType getType() {
		return type;
	}
}
