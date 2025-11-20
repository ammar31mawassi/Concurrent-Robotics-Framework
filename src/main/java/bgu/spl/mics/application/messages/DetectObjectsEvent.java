package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent implements Event<Boolean> {
    private final StampedDetectedObjects detectedObjects;
    private final int detectionTime;

    public DetectObjectsEvent(StampedDetectedObjects detectedObjects, int detectionTime) {
        this.detectedObjects = detectedObjects;
        this.detectionTime = detectionTime;
    }

    public StampedDetectedObjects getDetectedObjects() {
        return detectedObjects;
    }

    public int getDetectionTime() {
        return detectionTime;
    }

    @Override
    public String toString() {
        return "DetectObjectsEvent{" +
                "detectedObjects=" + detectedObjects +
                ", detectionTime=" + detectionTime +
                '}';
    }
}
