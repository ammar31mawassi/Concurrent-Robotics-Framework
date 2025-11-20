package bgu.spl.mics;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private final ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventMessages;
    private final ConcurrentHashMap<Event, Future> futuerMap;
    private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastMessages;
    private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> messageQueue;

    private MessageBusImpl() {
        eventMessages = new ConcurrentHashMap<>();
        futuerMap = new ConcurrentHashMap<>();
        broadcastMessages = new ConcurrentHashMap<>();
        messageQueue = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        eventMessages.putIfAbsent(type, new LinkedBlockingQueue<>());
        eventMessages.get(type).add(m);

    }

    @Override
    public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        broadcastMessages.putIfAbsent(type, new LinkedBlockingQueue<>());
        broadcastMessages.get(type).add(m);
    }

    @Override
    public synchronized <T> void complete(Event<T> e, T result) {
        futuerMap.get(e).resolve(result);
    }

    @Override
    public synchronized void sendBroadcast(Broadcast b) {
        synchronized (broadcastMessages) {
            if (broadcastMessages.containsKey(b.getClass())) {
                for (MicroService ms : broadcastMessages.get(b.getClass())) {
                    messageQueue.get(ms).add(b);
                }
            }
        }

    }


    @Override
    public synchronized <T> Future<T> sendEvent(Event<T> e) {
        if (eventMessages.containsKey(e.getClass()) && !eventMessages.get(e.getClass()).isEmpty()) {
            MicroService micro = eventMessages.get(e.getClass()).poll();
            if (micro == null) {
                return null;
            } else {
                try {
                    Future<T> future = new Future<>();
                    futuerMap.put(e, future);
                    if (messageQueue.get(micro) != null) {
                        messageQueue.get(micro).add(e);
                    }
                    eventMessages.get(e.getClass()).add(micro);
                    return future;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        messageQueue.put(m, new LinkedBlockingQueue<Message>());
    }

    @Override
    public void unregister(MicroService m) {
        synchronized (eventMessages) {
            Iterator<Class<? extends Event>> itr = eventMessages.keySet().iterator();
            while (itr.hasNext()) {
                Class<? extends Event> tmp = itr.next();
                if (eventMessages.containsKey(tmp)) {
                    eventMessages.get(tmp).remove(m);
                }
            }
        }
        synchronized (broadcastMessages) {
            Iterator<Class<? extends Broadcast>> iter3 = broadcastMessages.keySet().iterator();
            while (iter3.hasNext()) {
                Class<? extends Broadcast> tmp3 = iter3.next();
                if (broadcastMessages.containsKey(tmp3)) {
                    broadcastMessages.get(tmp3).remove(m);
                }
            }

        }
        synchronized (messageQueue) {
            messageQueue.remove(m);
        }

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return messageQueue.get(m).take();
    }

    public <T> T getResult(Event<T> e) {
        return (T) futuerMap.get(e).get();
    }

    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

}
