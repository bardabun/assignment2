package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final List<MicroServiceParameters> microsData = new ArrayList<>();
	Iterator<MicroServiceParameters> iter = microsData.iterator();
	protected int activeReaders = 0;
	protected int activeWriters = 0;
	protected int waitingWriters = 0;
	private final String AttackEvent = "AttackEvent";
	//protected int waitingSendBroadcast = 0;
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		try {
			beforeWrite();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(MicroServiceParameters micro: microsData){
			if(micro.getMicroServiceName().equals(m.getName()))
				micro.setEventType(type);
		}
		afterWrite();
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		try {
			beforeWrite();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(MicroServiceParameters micro: microsData){
			if(micro.getMicroServiceName().equals(m.getName()))
				micro.setBroadcastType(type);
		}
		afterWrite();
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		try {
			beforeWrite();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(MicroServiceParameters micro: microsData){
			if(micro.getBroadcastType().equals(b.getClass()))
				micro.getQ().add(b);
		}
		afterWrite();
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		try {
			beforeWrite();
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}
		roundRobinManner(e);
		afterWrite();
        return null;
	}

	private void roundRobinManner(Message e){
		if (e.toString().equals(AttackEvent)){
			for(MicroServiceParameters micro: microsData) {
				if(micro.getEventType().equals(AttackEvent) &&)
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
	public void register(MicroService m) {
		try {
			beforeWrite();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
