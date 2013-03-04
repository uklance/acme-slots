package com.lazan.acme.slots.internal;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import com.lazan.acme.slots.Bag;
import com.lazan.acme.slots.VoltService;

/**
 * Simulates the Volt WebService by sleeping before assigning a volt id.
 */
public class SleepingVoltService implements VoltService {
	private final AtomicInteger nextIncrement = new AtomicInteger(1);
	
	@Override
	public String getVoltId(Bag bag, Collection<Bag> matches) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		return "V" + nextIncrement.getAndIncrement();
	}
}
