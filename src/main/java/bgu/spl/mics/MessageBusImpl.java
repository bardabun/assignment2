package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final Map<String, LinkedList<Message>> registeredMicro = new ConcurrentHashMap<String, LinkedList<Message>>();
	private final Map<String, Vector<String>> eventTypeMicro = new ConcurrentHashMap<>();
	private final Map<String, Vector<String>> broadcastTypeMicro = new ConcurrentHashMap<>();
	//Round-Robin Manner fields:
	Queue<String> roundRobinQueue = new LinkedList<String>();
	Iterator<String> roundRobinIter = registeredMicro.keySet().iterator();

	protected int activeReaders = 0;
	protected int activeWriters = 0;
	protected int waitingWriters = 0;
	private final String AttackEvent = "AttackEvent";
	//protected int waitingSendBroadcast = 0;
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws InterruptedException {
		beforeWrite();

		if(registeredMicro.containsKey(m.getName())){
			if (!eventTypeMicro.containsKey(m.getName()))
				eventTypeMicro.put(m.getName(), new Vector<String>());

			Vector<String> microEventTypes = eventTypeMicro.get(m.getName());
			String eventName = type.getName();
			if (!microEventTypes.contains(eventName))
				microEventTypes.add(eventName);
		} else{
			System.out.println("You didn't register " + m.getName() + " yet");
		}
		afterWrite();
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws InterruptedException {
		beforeWrite();

		String micro = m.getName();
		if(registeredMicro.containsKey(m.getName())){
			if (!broadcastTypeMicro.containsKey(micro))
				eventTypeMicro.put(micro, new Vector<String>());

			Vector<String> microBroadcastTypes = broadcastTypeMicro.get(micro);
			String broadcastType = type.getName();
			if (!microBroadcastTypes.contains(broadcastType))
				microBroadcastTypes.add(broadcastType);
		} else{
			System.out.println("You didn't register " + m.getName() + " yet");
		}
		afterWrite();
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) throws InterruptedException {
		beforeWrite();

		for (Map.Entry<String, Vector<String>> entry : broadcastTypeMicro.entrySet()) {
			Vector<String> broadcastType = entry.getValue();
			if (broadcastType.contains(b.getType())) {

				String entryKey = entry.getKey();
				registeredMicro.computeIfPresent(entryKey, (key, microQueue) -> {
					microQueue.add(b);
					return microQueue;
				});

			}
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

	private void roundRobinManner(Message m){
		for (Map.Entry<String, Vector<String>> entry : eventTypeMicro.entrySet()) {
			if (entry.getValue().contains(m.toString())) {
				roundRobinQueue.add(entry.getKey());

			}
		}
	}

	@Override
	public void register(MicroService m) throws InterruptedException {
		beforeWrite();

		String microName = m.getName();
		if(!registeredMicro.containsKey(microName))
			registeredMicro.put(microName, new LinkedList<Message>());

		afterWrite();
	}

	@Override
	public void unregister(MicroService m) throws InterruptedException {
		beforeWrite();

		String microToRemove = m.getName();
		try{
			broadcastTypeMicro.remove(microToRemove);
			eventTypeMicro.remove(microToRemove);
			registeredMicro.remove(microToRemove);
		}catch(Exception e){
			System.out.println(e);
		}

		afterWrite();
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		beforeRead();
		Message output = null;
		try {
			Queue<Message> q = registeredMicro.get(m.getName());
			output = q.poll();
		}catch (NullPointerException e){
			System.out.println(m.getName() + " doesn't exist int the hashMap ");
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
