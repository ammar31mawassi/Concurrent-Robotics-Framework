package bgu.spl.mics.application.objects;

import java.util.List;

import java.util.LinkedList;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    // TODO: Define fields and methods.
    private int time;
    private LinkedList<DetectedObject> detectedObjects;
    public StampedDetectedObjects(int time, LinkedList<DetectedObject> list){
        this.time = time;
        this.detectedObjects = list;
    }

    public LinkedList<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public int getTime() {
        return time;
    }
}
