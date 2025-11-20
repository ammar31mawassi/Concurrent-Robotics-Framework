package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    public int getId() {
        return id;
    }

    // TODO: Define fields and methods.
    private int id;
    private int frequency;
    private STATUS status;
    private LinkedList<TrackedObject> lastTrackedObjects;
    private LiDarDataBase dataBase;
    private HashMap<Integer, LinkedList<TrackedObject>> trackedTime;

    public LiDarDataBase getDataBase() {
        return dataBase;
    }

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.lastTrackedObjects = new LinkedList<>();
        this.status = STATUS.UP;
        this.dataBase = LiDarDataBase.getinstance();
        this.trackedTime = new HashMap<Integer,LinkedList<TrackedObject>>();
    }

    public int getFrequency() {
        return frequency;
    }

    public LinkedList<TrackedObject> getTrackedTime(Integer key) {
        return trackedTime.get(key);
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public STATUS getStatus() {
        return status;
    }

    public StampedCloudPoints getCoordinates(String id, int time) {
        return dataBase.getCoordinates(id, time);
    }

    public LinkedList<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public void addTrackedObject(TrackedObject trackedObject, Integer time) {
        lastTrackedObjects.add(trackedObject);
        trackedTime.putIfAbsent(time, new LinkedList<TrackedObject>());
        trackedTime.get(time).addLast(trackedObject);
    }

    public boolean isLastToCheck(Integer key) {
        for (Integer check : trackedTime.keySet()) {
            if(check > key){
                return false;
            }
        }
        return true;
    }
}
