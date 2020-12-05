package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    //Objects under test
    private final MessageBus bus = new MessageBusImpl();
    MicroService m1 = new TestMicroService("first");
    MicroService m2 = new TestMicroService("second");

    @Test
    void testSendBroadcast() {  //<--!!! is that exception ok?bus.register(m1);
        bus.register(m1);

        Broadcast test = new Broadcast() {};

        m1.sendBroadcast(test);
        try {
            assertEquals(bus.awaitMessage(m1), test);
            assertEquals(bus.awaitMessage(m2), test);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testSendEvent() {
        Event<Boolean> test1 = new TestEventer();

        bus.subscribeEvent(TestEventer.class, m2);

        m1.sendEvent(test1);
        Message test2 = null;

        try {
            test2 = bus.awaitMessage(m2);
            assertEquals(test2, test1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    void testAwaitMessage() throws InterruptedException {

        Message test = bus.awaitMessage(m1);
        assertNotEquals(test,null);
    }

    @Test
    void complete() {
        Event<Boolean> test = new TestEventer();

        bus.subscribeEvent(TestEventer.class, m2);
        Future<Boolean> boolFuture = m1.sendEvent(test);

        assert boolFuture != null;
        boolFuture.resolve(true);
        bus.complete(test, true);
        assertTrue(boolFuture.get());

    }
}