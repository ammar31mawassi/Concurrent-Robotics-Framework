package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * <p>
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera"+camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
            if (camera.getStatus() == STATUS.UP) {
                currentTick = msg.getCurrentTick();
                Integer timeToGet = currentTick - camera.getFrequency();
                if(camera.getDetectedObjectsList(currentTick)!=null) {
                    for (DetectedObject check : camera.getDetectedObjectsList(currentTick).getDetectedObjects()) {
                        if (check.getId().equals("ERROR")) {
                            camera.setStatus(STATUS.ERROR);
                            CrashedBroadcast cr = new CrashedBroadcast(this.getName(), check.getDescription());
                            sendBroadcast(cr);
                            ErrorClass.getInstance().setError(cr);
                            terminate();
                            FusionSlam.getInstance().stats.setSystemRunTime(currentTick);
                            FusionSlam.getInstance().stats.isFinalCall();
                            break;
                        }
                    }
                }
                if (timeToGet > 0 && camera.getStatus()==STATUS.UP) {
                    StampedDetectedObjects lst = camera.getDetectedObjectsList(timeToGet);
                    if (lst != null) {
                        if (camera.getStatus() == STATUS.UP) {
                            DetectObjectsEvent ev = new DetectObjectsEvent(lst, timeToGet);
                            sendEvent(ev);
                            ErrorClass.getInstance().addLastFrameCamera(this.getName(),ev);
                            FusionSlam.getInstance().stats.addDetectedObjectsToStats(ev.getDetectedObjects().getDetectedObjects().size());
                        }
                    }
                }
                if (currentTick == camera.getMaxTime() && camera.getStatus() == STATUS.UP) {
                    camera.setStatus(STATUS.DOWN);
                    FusionSlam.getInstance().stats.setSystemRunTime(currentTick);
                    this.terminate();
                    sendBroadcast(new TerminatedBroadcast(getName()));
                }
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, ev -> {
            if (ev.getSource().equals("TimeService")) {
                terminate();
                FusionSlam.getInstance().stats.setSystemRunTime(currentTick);
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, cr -> {
            
            terminate();
        });
    }

}
