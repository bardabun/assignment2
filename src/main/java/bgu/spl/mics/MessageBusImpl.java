package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final Map<MicroService, LinkedList<Message>> registeredMicro = new ConcurrentHashMap<>();
	private final Map<Class<? extends Event<?>>, LinkedBlockingDeque<MicroService>> eventTypeMicro = new ConcurrentHashMap<>();
	private final Map<Class<? extends Broadcast>, LinkedBlockingDeque<MicroService>> broadcastTypeMicro = new ConcurrentHashMap<>();

	protected int activeReaders = 0;
	protected int activeWriters = 0;
	protected int waitingWriters = 0;
	//protected int waitingSendBroadcast = 0;

	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl() {
	}

	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws InterruptedException {
		beforeWrite();

		if(registeredMicro.containsKey(m)) {
			if (!eventTypeMicro.containsKey(type))
				eventTypeMicro.put(type, new LinkedBlockingDeque<>());

			eventTypeMicro.computeIfPresent(type, (key, microQueue) -> {
				microQueue.add(m);
				return microQueue;
			});
		} else{
			System.out.println("You didn't register " + m.getName() + " yet");
		}

		afterWrite();
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws InterruptedException {
		beforeWrite();

		if(registeredMicro.containsKey(m)) {
			if (!broadcastTypeMicro.containsKey(type))
				broadcastTypeMicro.put(type, new LinkedBlockingDeque<>());

			broadcastTypeMicro.computeIfPresent(type, (key, microQueue) -> {
				microQueue.add(m);
				return microQueue;
			});
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

		if(broadcastTypeMicro.containsKey(b)){
			for(MicroService micro : broadcastTypeMicro.get(b))
				registeredMicro.get(micro).add(b);
		} else
			System.out.println("Broadcast type: " + b + " has no registered MicroServices");

		afterWrite();
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
		beforeWrite();

		if(eventTypeMicro.containsKey(e) && !eventTypeMicro.get(e).isEmpty()) {

			LinkedBlockingDeque<MicroService> microsQueue = eventTypeMicro.get(e);
			MicroService micro = microsQueue.poll();
			registeredMicro.get(micro).add(e);
			microsQueue.add(micro);
		}
		afterWrite();
        return null;
	}

	@Override
	public void register(MicroService m) throws InterruptedException {
		beforeWrite();

		if(!registeredMicro.containsKey(m))
			registeredMicro.put(m, new LinkedList<Message>());

		afterWrite();
	}

	@Override
	public void unregister(MicroService m) throws InterruptedException {
		beforeWrite();

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
	public Message awaitMessage(MicroService m) throws InterruptedException {	//take method of blocking queue
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
