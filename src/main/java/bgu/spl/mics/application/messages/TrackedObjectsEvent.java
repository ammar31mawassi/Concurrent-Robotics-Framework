package bgu.spl.mics.application.messages;

import java.util.LinkedList;
import java.util.List;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<Void> {
    private final LinkedList<TrackedObject> trackedObjects;
    private final int trackingTime;

    public TrackedObjectsEvent(LinkedList<TrackedObject> trackedObjects, int trackingTime) {
        this.trackedObjects = trackedObjects;
        this.trackingTime = trackingTime;
    }

    public LinkedList<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    public int getTrackingTime() {
        return trackingTime;
    }

    @Override
    public String toString() {
        return "TrackedObjectsEvent{" +
                "trackedObjects=" + trackedObjects +
                ", trackingTime=" + trackingTime +
                '}';
    }
}
