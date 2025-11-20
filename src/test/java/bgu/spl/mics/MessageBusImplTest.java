package bgu.spl.mics;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService mockService1;
    private MicroService mockService2;
    private Event mockEvent;
    private Broadcast mockBroadcast;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        mockService1 = new MicroService("MockService1") {
            @Override
            protected void initialize() {
            }
        };
        mockService2 = new MicroService("MockService2") {
            @Override
            protected void initialize() {
            }
        };
        mockEvent = new DetectObjectsEvent(null, 1);
        mockBroadcast = new TerminatedBroadcast("test");
        messageBus.register(mockService1);
        messageBus.register(mockService2);
    }

    @AfterEach
    void tearDown() {
        messageBus.unregister(mockService1);
        messageBus.unregister(mockService2);
    }

    @Test
    void subscribeEvent() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, mockService1);
        Future<String> future = messageBus.sendEvent(mockEvent);
        assertNotNull(future);
    }

    @Test
    void subscribeBroadcast() {
        messageBus.subscribeBroadcast(mockBroadcast.getClass(), mockService1);
        messageBus.sendBroadcast(mockBroadcast);
        try {
            Message received = messageBus.awaitMessage(mockService1);
            assertEquals(mockBroadcast, received);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        }
    }

    @Test
    void complete() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, mockService1);
        Future<String> future = messageBus.sendEvent(mockEvent);
        assertNotNull(future);
        messageBus.complete(mockEvent, "Result");
        assertTrue(future.isDone());
        assertEquals("Result", future.get());
    }

    @Test
    void sendBroadcast() {
        messageBus.subscribeBroadcast(mockBroadcast.getClass(), mockService1);
        messageBus.sendBroadcast(mockBroadcast);
        try {
            Message received = messageBus.awaitMessage(mockService1);
            assertEquals(mockBroadcast, received);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        }
    }

    @Test
    void sendEvent() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, mockService1);
        Future<String> future = messageBus.sendEvent(mockEvent);
        assertNotNull(future);
        try {
            Message received = messageBus.awaitMessage(mockService1);
            assertEquals(mockEvent, received);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        }
    }


    @Test
    void awaitMessage() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, mockService1);
        messageBus.sendEvent(mockEvent);
        try {
            Message received = messageBus.awaitMessage(mockService1);
            assertEquals(mockEvent, received);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        }
    }

    @Test
    void getResult() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, mockService1);
        Future<String> future = messageBus.sendEvent(mockEvent);
        messageBus.complete(mockEvent, "Result");
        assertEquals("Result", messageBus.getResult(mockEvent));
    }

    @Test
    void getInstance() {
        MessageBusImpl instance1 = MessageBusImpl.getInstance();
        MessageBusImpl instance2 = MessageBusImpl.getInstance();
        assertSame(instance1, instance2);
    }
}
