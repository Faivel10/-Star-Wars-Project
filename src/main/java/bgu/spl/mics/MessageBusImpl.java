package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private final ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>>  messagesToMicro;
	private final ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microQueue;
	private final ConcurrentHashMap<Event,Future> eventsWithResults;

	private MessageBusImpl()
	{
		messagesToMicro=new ConcurrentHashMap<>();
		microQueue=new ConcurrentHashMap<>();
		eventsWithResults=new ConcurrentHashMap<>();
	}

	/*creates private sub class to make MessageBus to be Singleton
	and safe-thread.
	 */
	private static class SingeltonMessageBus{
		private static MessageBusImpl instance=new MessageBusImpl();
	}
	public static MessageBusImpl getInstance(){
		return SingeltonMessageBus.instance;
	}

	@Override
	public   <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//if the event isnt saved already- save it and add the microservies as one that can do it.
		if (!messagesToMicro.containsKey(type)) {
			messagesToMicro.put(type, new ConcurrentLinkedQueue<>());
		}
		messagesToMicro.get(type).add(m);
	}
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//if the broadcast isnt saved already- save it and add the microservies as one that can do it.
		if(!messagesToMicro.containsKey(type)) {
			messagesToMicro.put(type,new ConcurrentLinkedQueue<>());
		}
		messagesToMicro.get(type).add(m);

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		//resolve the future object with the result given.
		eventsWithResults.get(e).resolve(result);
		}


	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		//if there exists a microserviese that can get the broadcast we send it and then we alert that the broadcast was send.
		if(messagesToMicro.containsKey(b.getClass()) && !messagesToMicro.get(b.getClass()).isEmpty())
		{
			for(MicroService m :messagesToMicro.get(b.getClass()))
			{
				microQueue.get(m).add(b);
			}
		}
		notifyAll();
	}


	@Override
	public  synchronized <T> Future<T> sendEvent(Event<T> e) {

		//if there is a microservice that can handle the event we send it, otherwise we just return null.
	 if(messagesToMicro.containsKey(e.getClass()) && !messagesToMicro.get(e.getClass()).isEmpty())
		{
			Future<T> x=new Future<T>();
			//to know which event the future belongs to
			eventsWithResults.putIfAbsent(e,x);
			//to protect the rotation of the microservices used we remove the first one which is used and then put it in the back.
			MicroService m =messagesToMicro.get(e.getClass()).remove();
			microQueue.get(m).add(e);
			messagesToMicro.get(e.getClass()).add(m);
			//notifying that the event was send.
			notifyAll();
			return x;
		}
	 //if there is no microservice to handle the event, we return null.
		return null;
	}

	@Override
	//creates message queue to m
	public void register(MicroService m) {
		//we add a new queue for the microservice. using hashmap and the micro as key.
		microQueue.put(m,new ConcurrentLinkedQueue<>());

	}

	@Override
	public void unregister(MicroService m) {
		//removing the queue of m from the micro queues.
		microQueue.remove(m);

		//we search each event that may have m listed there as a handler of the event and we remove it.
		for(ConcurrentLinkedQueue<MicroService> micros:messagesToMicro.values())
		{
			micros.remove(m);

		}
	}

	@Override
	public synchronized Message  awaitMessage(MicroService m) throws InterruptedException {
		Message q=null;
		//we wait until we get some message to handle.
		while (microQueue.get(m).isEmpty()) {
				wait();
			}
		return microQueue.get(m).remove();
	}


}

