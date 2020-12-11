package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import jdk.internal.misc.FileSystemOption;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<String, String> registeredMicro = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Vector<String>> eventTypeMicro = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Vector<String>> broadcastTypeMicro = new ConcurrentHashMap<>();


	protected int activeReaders = 0;
	protected int activeWriters = 0;
	protected int waitingWriters = 0;
	private final String AttackEvent = "AttackEvent";
	//protected int waitingSendBroadcast = 0;
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws InterruptedException {
		beforeWrite();

		try{
			if (!eventTypeMicro.containsKey(m.getName()))
				eventTypeMicro.put(m.getName(), new Vector<String>());

			Vector<String> microEventTypes = eventTypeMicro.get(m.getName());
			String eventName = type.getName();
			if (!microEventTypes.contains(eventName))
				microEventTypes.add(eventName);
		} catch(Exception e){
			System.out.println("We Have A Problem :/\nMaybe we didn't register " + m.getName() + " yet?");
		}
		afterWrite();
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws InterruptedException {
		beforeWrite();

		String micro = m.getName();
		try{
			if (!broadcastTypeMicro.containsKey(micro))
				eventTypeMicro.put(micro, new Vector<String>());

			Vector<String> microBroadcastTypes = broadcastTypeMicro.get(micro);
			String broadcastType = type.getName();
			if (!microBroadcastTypes.contains(broadcastType))
				microBroadcastTypes.add(broadcastType);
		} catch(Exception e){
			System.out.println("We Have A Problem :/\nMaybe we didn't register " + micro + " yet?");
		}
		afterWrite();
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) throws InterruptedException {
		beforeWrite();

		for(MicroServiceParameters micro: microsData){
			if(micro.getBroadcastType().equals(b.getClass()))
				micro.getQ().add(b);
		}
		afterWrite();
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
		beforeWrite();
		roundRobinManner(e);
		afterWrite();
        return null;
	}

	private void roundRobinManner(Message e){
		if (e.toString().equals(AttackEvent)){
			for(MicroServiceParameters micro: microsData) {
				if(micro.getEventType().equals(AttackEvent))
					micro.getQ().add(e);
			}
		}
		else {
			for(MicroServiceParameters micro: microsData) {
				micro.getQ().add(e);
			}
		}
	}

	@Override
	public void register(MicroService m) throws InterruptedException {
		beforeWrite();

		String name = m.getName();
		microsData.add(new MicroServiceParameters(name));

		afterWrite();
	}

	@Override
	public void unregister(MicroService m) {
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		beforeRead();
		Message output = null;
		for(MicroServiceParameters micro: microsData)
			if(micro.getMicroServiceName().equals(m.getName())){
				output = (micro.getQ()).poll();
			}
		afterRead();
		return output;
	}

	protected synchronized void beforeRead() throws InterruptedException {
		while(!(waitingWriters == 0 && activeWriters == 0))
			wait();
		activeReaders++;
	}
	protected synchronized void afterRead() {
		activeReaders--;
		notifyAll();
	}
	protected synchronized void beforeWrite() throws InterruptedException {
		waitingWriters++;
		while (!(activeReaders == 0 && activeWriters == 0)) {
			wait();
		}
		waitingWriters++;
		activeWriters++;
	}
	protected synchronized void afterWrite() {
		activeWriters--;
		notifyAll();
	}
}
