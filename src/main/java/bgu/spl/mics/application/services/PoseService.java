package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;


/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    private GPSIMU gpsimu;
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        // TODO Implement this
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeBroadcast(TickBroadcast.class, tc -> {
            gpsimu.setCurrTick(tc.getCurrentTick());
            if (gpsimu.getStatus() == STATUS.UP) {
                Pose pose = gpsimu.getPoseNow();

                sendEvent(new PoseEvent(pose));
                
            }
            if(gpsimu.getStatus() == STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                terminate();
                FusionSlam.getInstance().stats.setSystemRunTime(currentTick);
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, ev -> {
            if (ev.getSource().equals("TimeService")) {
                terminate();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, cr -> {
            terminate();
        });
    }
}
