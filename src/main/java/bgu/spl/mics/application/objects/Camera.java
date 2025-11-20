package bgu.spl.mics.application.objects;

import java.util.HashMap;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private Integer maxTime;
    private int frequency;
    private STATUS status;
    private HashMap<Integer, StampedDetectedObjects> detectedObjectsList;

    /**
     * Constructor for Camera.
     * 
     * @PRE id > 0 && frequency > 0
     * @POST id is set, frequency is set, status is STATUS.UP,
     *       detectedObjectsList is initialized as empty, maxTime is 0
     */
    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.detectedObjectsList = new HashMap<>();
        this.maxTime = 0;
    }

    /**
     * Returns the maximum timestamp of detected objects.
     * 
     * @PRE None
     * @POST Returns maxTime
     */
    public Integer getMaxTime() {
        return maxTime;
    }

    /**
     * Returns the camera ID.
     * 
     * @PRE None
     * @POST Returns the value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves detected objects by timestamp key.
     * 
     * @PRE key != null
     * @POST Returns StampedDetectedObjects if key exists, otherwise null
     */
    public StampedDetectedObjects getDetectedObjectsList(Integer key) {
        return detectedObjectsList.get(key);
    }

    /**
     * Adds a detected object to the list.
     * 
     * @PRE toAdd != null && toAdd.getTime() > 0
     * @POST Adds toAdd to detectedObjectsList, updates maxTime if needed
     */
    public void addDetectedObjects(StampedDetectedObjects toAdd) {
        detectedObjectsList.put(toAdd.getTime(), toAdd);
        if (toAdd.getTime() > maxTime) {
            maxTime = toAdd.getTime();
        }
    }

    /**
     * Sets the status of the camera.
     * 
     * @PRE status != null
     * @POST Updates the camera status
     */
    public void setStatus(STATUS status) {
        this.status = status;
    }

    /**
     * Returns the status of the camera.
     * 
     * @PRE None
     * @POST Returns the current status
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Returns the frequency of the camera.
     * 
     * @PRE None
     * @POST Returns the frequency
     */
    public int getFrequency() {
        return frequency;
    }
}
