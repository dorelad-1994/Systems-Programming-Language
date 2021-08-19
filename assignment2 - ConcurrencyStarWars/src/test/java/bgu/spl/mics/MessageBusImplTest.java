package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetInstance() {
        assertEquals(messageBus, MessageBusImpl.getInstance());
    }

    @Test
    void testComplete() {
        AttackEvent attackEvent = new AttackEvent(new Integer[0], 1);
        MicroService microService = new HanSoloMicroservice();
        messageBus.register(microService);
        messageBus.subscribeEvent(attackEvent.getClass(),microService);
        Future<Boolean> f = messageBus.sendEvent(attackEvent);
        while (true) {
            try {
                Message message = messageBus.awaitMessage(microService);
                messageBus.complete((AttackEvent)message, true);
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }
        assertTrue(f.isDone());
        assertTrue(f.get());
        messageBus.unregister(microService);
    }

    @Test
    void testSendBroadcast() { //Also tests register, subscribeBroadcast and awaitMessage
        TerminateMissionBroadcast b = new TerminateMissionBroadcast();
        MicroService c = new C3POMicroservice();
        messageBus.register(c);
        messageBus.subscribeBroadcast(b.getClass(), c);
        messageBus.sendBroadcast(b);
        while (true) {
            try {
                assertEquals(b, messageBus.awaitMessage(c));
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }
        messageBus.unregister(c);
    }

    @Test
    void testSendEvent() { //Also tests register, subscribeEvent and awaitMessage
        AttackEvent a = new AttackEvent(new Integer[0], 1);
        MicroService c = new C3POMicroservice();
        messageBus.register(c);
        messageBus.subscribeEvent(a.getClass(), c);
        messageBus.sendEvent(a);
        while (true) {
            try {
                assertEquals(a, messageBus.awaitMessage(c));
                break;
            } catch (InterruptedException e) {
                continue;
            }
        }
        messageBus.unregister(c);
    }
}