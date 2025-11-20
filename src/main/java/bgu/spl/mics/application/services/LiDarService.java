package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.LinkedList;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * <p>
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    private LiDarWorkerTracker liDarWorkerTracker;

    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarWorkerTracker" + LiDarWorkerTracker.getId());
        this.liDarWorkerTracker = LiDarWorkerTracker;
    }

    private ArrayList<CloudPoint> convertCoords(StampedCloudPoints st) {
        ArrayList<CloudPoint> result = new ArrayList<>();
        for (Double[] points : st.getCloudPoints()) {
            result.add(new CloudPoint(points[0], points[1]));
        }
        return result;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeBroadcast(TerminatedBroadcast.class, ev -> {
            if (ev.getSource().equals("TimeService")) {
                terminate();
                FusionSlam.getInstance().stats.setSystemRunTime(currentTick);
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, cr -> {
            terminate();
        });
        subscribeEvent(DetectObjectsEvent.class, st -> {

            if (liDarWorkerTracker.getStatus() == STATUS.UP) {
                Integer time = st.getDetectionTime();
                for (DetectedObject toCheck : st.getDetectedObjects().getDetectedObjects()) {
                    StampedCloudPoints coordinates = liDarWorkerTracker.getCoordinates(toCheck.getId(), time);
                    if (coordinates != null) {
                        if (coordinates.getId().equals("ERROR")) {
                            liDarWorkerTracker.setStatus(STATUS.ERROR);
                            CrashedBroadcast cr = new CrashedBroadcast(this.getName(), "LiDar Worker ERROR");
                            ErrorClass.getInstance().setError(cr);
                            sendBroadcast(cr);
                            terminate();
                            FusionSlam.getInstance().stats.setSystemRunTime(currentTick);
                            FusionSlam.getInstance().stats.isFinalCall();
                            break;
                        }
                        ArrayList<CloudPoint> cloudPoints = convertCoords(coordinates);
                        TrackedObject trackedObject = new TrackedObject(toCheck.getId(), time, toCheck.getDescription(), cloudPoints);
                        liDarWorkerTracker.addTrackedObject(trackedObject, time);
                        if (liDarWorkerTracker.getDataBase().isLastInput(coordinates)) {
                            liDarWorkerTracker.setStatus(STATUS.DOWN);
                            FusionSlam.getInstance().stats.setSystemRunTime(this.currentTick);
                            sendBroadcast(new TerminatedBroadcast(this.getName()));
                            terminate();
                            break;
                        }
                    }
                }
                if (liDarWorkerTracker.getFrequency() == 0 || currentTick - liDarWorkerTracker.getFrequency() == time) {
                    LinkedList<TrackedObject> trackedObjects = liDarWorkerTracker.getTrackedTime(time);
                    if (trackedObjects != null) {
                        FusionSlam.getInstance().stats.addTrackedObjectsToStats(trackedObjects.size());
                        TrackedObjectsEvent tr = new TrackedObjectsEvent(liDarWorkerTracker.getTrackedTime(time), time);
                        ErrorClass.getInstance().addLastFrameLiDar(this.getName(), tr);
                        sendEvent(tr);
                    }
                }

            }
            complete(st, true);
        });
        subscribeBroadcast(TickBroadcast.class, tc -> {
            currentTick = tc.getCurrentTick();
            if (liDarWorkerTracker.getFrequency() != 0) {
                Integer timeToGet = currentTick - liDarWorkerTracker.getFrequency();
                if (timeToGet > 0) {
                    LinkedList<TrackedObject> trackedObjects = liDarWorkerTracker.getTrackedTime(timeToGet);
                    if (trackedObjects != null) {
                        TrackedObjectsEvent tr = new TrackedObjectsEvent(trackedObjects, timeToGet);
                        sendEvent(tr);
                        ErrorClass.getInstance().addLastFrameLiDar(this.getName(), tr);
                        FusionSlam.getInstance().stats.addTrackedObjectsToStats(trackedObjects.size());
                        if (liDarWorkerTracker.isLastToCheck(timeToGet)) {
                            liDarWorkerTracker.setStatus(STATUS.DOWN);
                            FusionSlam.getInstance().stats.setSystemRunTime(this.currentTick);
                            sendBroadcast(new TerminatedBroadcast(this.getName()));
                            terminate();
                        }
                    }
                }
            }
        });
    }
}
