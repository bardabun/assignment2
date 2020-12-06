package bgu.spl.mics;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private List<MicroServiceParameters> microsData = new ArrayList<>();

	protected int activeReaders = 0;
	protected int activeWriters = 0;
	protected int waitingWriter = 0;
	protected int waitingSendBroadcast = 0;
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		String name = m.getName();
		microsData.add(new MicroServiceParameters(name));
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

		return output;
	}

	protected synchronized void beforeRead() throws InterruptedException {
		while(!(waitingSendBroadcast == 0 && waitingWriter == 0 && activeWriters == 0))
			wait();
		activeReaders++;
	}
	protected synchronized  void afterRead() {
		activeReaders--;
		notifyAll();
	}
}
