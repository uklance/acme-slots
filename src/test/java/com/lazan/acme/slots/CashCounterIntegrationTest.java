package com.lazan.acme.slots;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.lazan.acme.slots.internal.BagTotalEventHandler;
import com.lazan.acme.slots.internal.BagVoltEventHandler;
import com.lazan.acme.slots.internal.DisruptorCashCounterInputListener;
import com.lazan.acme.slots.internal.InMemoryCashCounterRepository;
import com.lazan.acme.slots.internal.SleepingVoltService;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class CashCounterIntegrationTest {
	private static final int RING_SIZE = 1024 * 1024;
	private CashCounterInputListener inputListener;
	private Set<String> unmatchedBagIds;
	private Map<String, String> bagVolts;
	private Map<DenominationType, Integer> runningTotals;
	
	@Before
	public void before() {
		unmatchedBagIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
		bagVolts = new ConcurrentHashMap<String, String>();
		runningTotals = new ConcurrentHashMap<DenominationType, Integer>();

		VoltService voltService = new SleepingVoltService();
		CashCounterOutputListener outputListener = new MockCashCounterOutputListener();
		CashCounterRepository repository = new InMemoryCashCounterRepository();
		EventHandler<InputEvent> bagTotalHandler = new BagTotalEventHandler(repository, outputListener);
		EventHandler<InputEvent> bagVoltHandler = new BagVoltEventHandler(repository, voltService);

		Executor executor = Executors.newFixedThreadPool(10);
		Disruptor<InputEvent> disruptor = new Disruptor<InputEvent>(InputEvent.EVENT_FACTORY, RING_SIZE, executor);
		disruptor.handleEventsWith(bagTotalHandler).then(bagVoltHandler);
		
		RingBuffer<InputEvent> ringBuffer = disruptor.start();
		inputListener = new DisruptorCashCounterInputListener(ringBuffer, repository);
	}	
	
	@Test
	public void testMatch() {
		sendBag("B1", Denomination.NOTE_1);
		sendBag("B2", Denomination.COIN_10, Denomination.COIN_20, Denomination.COIN_20, Denomination.COIN_50);
		
		sleep(5 * 1000);
		
		assertSameVolt("B1", "B2");
		assertEquals(new Integer(100), runningTotals.get(DenominationType.COIN));
		assertEquals(new Integer(100), runningTotals.get(DenominationType.NOTE));
	}
	
	private void assertSameVolt(String... bagIds) {
		String firstVoltId = null;
		for (String bagId : bagIds) {
			String voltId = bagVolts.get(bagId);
			assertNotNull("Checking volt for bagId " + bagId, voltId);
			if (firstVoltId == null) {
				firstVoltId = voltId;
			} else {
				assertEquals("Checking volt for bagId " + bagId, firstVoltId, voltId);
			}
		}
	}
	
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
	}
	
	private void sendBag(String bagId, Denomination... denoms) {
		inputListener.startBag(bagId);
		for (Denomination denom : denoms) {
			inputListener.bagEntry(bagId, denom);
		}
		inputListener.endBag(bagId);
	}

	class MockCashCounterOutputListener implements CashCounterOutputListener {
		@Override
		public void bagNotMatched(String bagId, Integer bagTotalCents) {
			unmatchedBagIds.add(bagId);
		}
		
		@Override
		public void bagVolt(String bagId, String voltId) {
			bagVolts.put(bagId,  voltId);
		}
		
		@Override
		public void runningTotal(DenominationType denominationType, Integer runningTotalCents) {
			runningTotals.put(denominationType, runningTotalCents);
		}
	}
}
