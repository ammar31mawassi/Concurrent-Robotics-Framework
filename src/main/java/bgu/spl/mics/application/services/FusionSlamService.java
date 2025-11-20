package bgu.spl.mics.application.services;

import java.util.LinkedList;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;


/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * <p>
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    private FusionSlam fusionSlam;
    private LinkedList<TrackedObjectsEvent> waiting;

    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        waiting = new LinkedList<>();
    }

    private LinkedList<CloudPoint> calculatePoints(TrackedObject trackedObject) {
        LinkedList<CloudPoint> answer = new LinkedList<>();
        double xGlobal = 0, yGlobal = 0;
        Pose currPose = fusionSlam.isPoseAvailable(trackedObject.getTime());


        if (currPose != null) {
            double thetaRad = Math.toRadians(currPose.getYaw());

            for (CloudPoint point : trackedObject.getCoordinates()) {

                xGlobal = (Math.cos(thetaRad) * point.getX()) - (Math.sin(thetaRad) * point.getY()) + currPose.getX();

                yGlobal = (Math.sin(thetaRad) * point.getX()) + (Math.cos(thetaRad) * point.getY()) + currPose.getY();

                CloudPoint calculatedPoint = new CloudPoint(xGlobal, yGlobal);
                answer.add(calculatedPoint);

            }

        }
        return answer;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeBroadcast(TerminatedBroadcast.class, ev -> {
            if (ev.getSource().equals("TimeService")) {
                fusionSlam.stats.setSystemRunTime(currentTick);
                terminate();
            }
            active--;
            if (active == 0) {
                sendBroadcast(new TerminatedBroadcast(this.getName()));

                terminate();
                fusionSlam.stats.setSystemRunTime(currentTick);
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, cr -> {
            terminate();
        });

        subscribeEvent(PoseEvent.class, pe -> {
            Pose currPose = pe.getPose();
            fusionSlam.addPose(currPose);
            if (!waiting.isEmpty()) {
                TrackedObjectsEvent toTrack;
                while (!waiting.isEmpty() && waiting.getFirst().getTrackingTime() <= currPose.getTime()) {
                    toTrack = waiting.removeFirst();
                    for (TrackedObject currObject : toTrack.getTrackedObjects()) {
                        LinkedList<CloudPoint> coords = calculatePoints(currObject);
                        if (!coords.isEmpty())
                            fusionSlam.addLandMark(currObject.getId(), currObject.getDescription(), coords);
                    }
                }
            }
            complete(pe, null);
        });

        subscribeEvent(TrackedObjectsEvent.class, tr -> {
            if (fusionSlam.isPoseAvailable(tr.getTrackingTime()) != null) {

                if (tr.getTrackedObjects() != null) {
                    for (TrackedObject currObject : tr.getTrackedObjects()) {
                        LinkedList<CloudPoint> coords = calculatePoints(currObject);
                        if (!coords.isEmpty())
                            fusionSlam.addLandMark(currObject.getId(), currObject.getDescription(), coords);
                    }
                }else{
                    System.out.println("was null");
                }
            } else {
                waiting.addLast(tr);
            }
        });

        subscribeBroadcast(TickBroadcast.class, (br) -> {
            currentTick = br.getCurrentTick();
        });
    }

    public void setStatisticalFolder(StatisticalFolder stats) {
        this.fusionSlam.setStatisticalFolder(stats);
    }

    private int active;

    public void setActiveSensors(int activeSensors) {
        active = activeSensors;
    }
}
