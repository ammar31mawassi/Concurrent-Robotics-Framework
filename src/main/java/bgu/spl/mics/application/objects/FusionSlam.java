package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {


    /**
     * Retrieves the list of landmarks.
     *
     * @PRE None
     * @POST Returns a non-null list of landmarks.
     */
    public ArrayList<LandMark> getLandmarks() {
        return landmarks;
    }

    // Singleton instance holder
    private static class FusionSlamHolder {
        // TODO: Implement singleton instance logic.
        private static FusionSlam instance = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public FusionSlam() {
        landmarks = new ArrayList<>();
        Poses = new ArrayList<>();
        hashLand = new HashMap<>();
    }

    public StatisticalFolder stats;
    private ArrayList<LandMark> landmarks;
    private ArrayList<Pose> Poses;
    private HashMap<String, LandMark> hashLand;

    /**
     * Sets the statistical folder.
     *
     * @PRE stats != null
     * @POST The stats field is updated with the given StatisticalFolder instance.
     */
    public void setStatisticalFolder(StatisticalFolder stats) {
        this.stats = stats;
    }

      /**
     * Checks if a pose is available for the given time index.
     *
     * @PRE time > 0
     * @POST Returns the Pose at the given time if available; otherwise, returns null.
     */
    public Pose isPoseAvailable(int time) {
        if (time > Poses.size()) {
            return null;
        }
        return Poses.get(time - 1);
    }

     /**
     * Adds a pose to the list.
     *
     * @PRE toAdd != null
     * @POST The given Pose is appended to the Poses list.
     */

    public void addPose(Pose toAdd) {
        Poses.add(toAdd);
    }
    /**
     * Adds or updates a landmark.
     *
     * @PRE id != null && !id.isEmpty() && desc != null && coords != null
     * @POST If the landmark ID exists, its coordinates are updated.
     *       Otherwise, a new landmark is added to landmarks and hashLand,
     *       and stats is updated to reflect the new landmark count.
     */

    public void addLandMark(String id, String desc, LinkedList<CloudPoint> coords) {
        if (hashLand.containsKey(id)) {
            double newX ,newY;
            LandMark toEdit = hashLand.get(id);
            LinkedList<CloudPoint> oldCoords = toEdit.getCoordinates(); 
            for (int i = 0; i < oldCoords.size() && i < coords.size(); i++) {
                newX = (oldCoords.get(i).getX() + coords.get(i).getX()) / 2;
                newY = (oldCoords.get(i).getY() + coords.get(i).getY()) / 2;
                oldCoords.get(i).setX(newX);
                oldCoords.get(i).setY(newY);
            }
        }else {
            LandMark toAdd = new LandMark(id, desc, coords);
            landmarks.add(toAdd);
            hashLand.put(id, toAdd);
            stats.addLandmarkToStats();
        }
    }
}
