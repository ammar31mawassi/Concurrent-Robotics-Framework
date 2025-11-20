package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    private Camera camera;

    @BeforeEach
    void setUp() {
        camera = new Camera(1, 60);
    }

    @Test
    void getMaxTime() {
        assertEquals(0, camera.getMaxTime());
        LinkedList<DetectedObject> objects1 = new LinkedList<>();
        objects1.add(new DetectedObject("1", "Object1 Description"));
        camera.addDetectedObjects(new StampedDetectedObjects(5, objects1));
        assertEquals(5, camera.getMaxTime());

        LinkedList<DetectedObject> objects2 = new LinkedList<>();
        objects2.add(new DetectedObject("2", "Object2 Description"));
        camera.addDetectedObjects(new StampedDetectedObjects(10, objects2));
        assertEquals(10, camera.getMaxTime());
    }

    @Test
    void getId() {
        assertEquals(1, camera.getId());
    }

    @Test
    void getDetectedObjectsList() {
        assertNull(camera.getDetectedObjectsList(1));

        LinkedList<DetectedObject> objects = new LinkedList<>();
        objects.add(new DetectedObject("1", "Object1 Description"));
        StampedDetectedObjects detectedObject = new StampedDetectedObjects(1, objects);
        camera.addDetectedObjects(detectedObject);
        assertEquals(detectedObject, camera.getDetectedObjectsList(1));
    }

    @Test
    void addDetectedObjects() {
        LinkedList<DetectedObject> objects1 = new LinkedList<>();
        objects1.add(new DetectedObject("1", "Object1 Description"));
        StampedDetectedObjects detectedObject1 = new StampedDetectedObjects(1, objects1);

        LinkedList<DetectedObject> objects2 = new LinkedList<>();
        objects2.add(new DetectedObject("2", "Object2 Description"));
        StampedDetectedObjects detectedObject2 = new StampedDetectedObjects(2, objects2);

        camera.addDetectedObjects(detectedObject1);
        assertEquals(detectedObject1, camera.getDetectedObjectsList(1));

        camera.addDetectedObjects(detectedObject2);
        assertEquals(detectedObject2, camera.getDetectedObjectsList(2));
        assertEquals(2, camera.getMaxTime());
    }

    @Test
    void setStatus() {
        assertEquals(STATUS.UP, camera.getStatus());
        camera.setStatus(STATUS.DOWN);
        assertEquals(STATUS.DOWN, camera.getStatus());
    }

    @Test
    void getStatus() {
        assertEquals(STATUS.UP, camera.getStatus());
        camera.setStatus(STATUS.DOWN);
        assertEquals(STATUS.DOWN, camera.getStatus());
    }

    @Test
    void getFrequency() {
        assertEquals(60, camera.getFrequency());
    }
}
