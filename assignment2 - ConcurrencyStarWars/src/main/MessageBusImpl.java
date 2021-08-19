package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static class MBSingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private volatile ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> messageQs;
    private volatile ConcurrentHashMap<Class<? extends Message>, Queue<MicroService>> subscriptions;
    private volatile ConcurrentHashMap<Event, Future> futures;

    private MessageBusImpl() {
        messageQs = new ConcurrentHashMap<>();
        subscriptions = new ConcurrentHashMap<>();
        futures = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return MBSingletonHolder.instance;
    }

    @Override

    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        Queue<MicroService> l = subscriptions.computeIfAbsent(type, k -> new LinkedList<>());// if no one is subscribed, create a new subscription list for this event
        synchronized (l) {
            l.add(m);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        Queue<MicroService> l = subscriptions.computeIfAbsent(type, k -> new LinkedList<>());// if no one is subscribed, create a new subscription list for this broadcast
        synchronized (l) {
            l.add(m);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        Future<T> f = futures.get(e);
        if (f != null) {
            f.resolve(result);
            futures.remove(e);// remove te completed event and it's future
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        Queue<MicroService> l = subscriptions.get(b.getClass());
        if (l != null) {// otherwise no one is subscribed
            synchronized (l) {
                Iterator<MicroService> iter = l.iterator();
                while (iter.hasNext()) {
                    MicroService m = iter.next();
                    LinkedBlockingQueue<Message> q = messageQs.get(m);
                    try {
                        q.put(b);
                    } catch (InterruptedException x) {
                    }
                }
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Queue<MicroService> l = subscriptions.get(e.getClass());
        if (l != null) { // otherwise no one is subscribed
            MicroService m;
            synchronized (l) {
                m = l.poll();
                l.add(m);//round robin implementation
            }
            Future<T> f = new Future<>();
            futures.put(e, f);
            LinkedBlockingQueue<Message> q = messageQs.get(m);
            try {
                q.put(e);
            } catch (InterruptedException x) {
            }
            return f;
        } else {
            return null;
        }
    }

    @Override
    public void register(MicroService m) {
        messageQs.put(m, new LinkedBlockingQueue<Message>());
    }

    @Override
    public void unregister(MicroService m) {
        for (Queue<MicroService> l : subscriptions.values()) {
            synchronized (l) {
                l.remove(m);
                if (l.isEmpty()) {
                    subscriptions.entrySet().removeIf(entry -> l.equals(entry.getValue()));
                    //remove the empty list from the hash table
                }
            }
        }
        messageQs.remove(m);
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> q = messageQs.get(m);
        return q.take();// waits if queue is empty
    }

}
//mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="/home/spl211/SPL/assignment2/input.json /home/spl211/SPL/assignment2/output.json"
//TODO delete