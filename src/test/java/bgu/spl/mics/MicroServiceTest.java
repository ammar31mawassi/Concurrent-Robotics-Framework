package bgu.spl.mics;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MicroServiceTest {

    private MicroService microService;
    private MessageBusImpl messageBus;
    private Event mockEvent;
    private Broadcast mockBroadcast;
    private Future<String> mockFuture;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService = new MicroService("TestMicroService") {
            @Override
            protected void initialize() {
                subscribeEvent(DetectObjectsEvent.class, (e) -> complete(e, true));
                subscribeBroadcast(mockBroadcast.getClass(), (b) -> System.out.println("Broadcast received"));
            }
        };
        mockEvent = new DetectObjectsEvent(null, 1);
        mockBroadcast = new Broadcast() {};
        mockFuture = new Future<>();
    }

    @Test
    void subscribeEvent() {
        microService.subscribeEvent(mockEvent.getClass(), (e) -> messageBus.complete(e, true));
        assertNotNull(messageBus.sendEvent(mockEvent));
    }

    @Test
    void subscribeBroadcast() {
        messageBus.register(microService);
        microService.subscribeBroadcast(mockBroadcast.getClass(), (b) -> System.out.println("Broadcast received"));
        messageBus.sendBroadcast(mockBroadcast);
        try {
            Message received = messageBus.awaitMessage(microService);
            assertEquals(mockBroadcast, received);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        }
        messageBus.unregister(microService);
    }

    @Test
    void sendEvent() {
        messageBus.register(microService);
        microService.subscribeEvent(DetectObjectsEvent.class, (e) -> {messageBus.complete(e, true);});
        Future<String> future = microService.sendEvent(mockEvent);
        assertNotNull(future);
        messageBus.unregister(microService);
    }

    @Test
    void getName() {
        assertEquals("TestMicroService", microService.getName());
    }
}
