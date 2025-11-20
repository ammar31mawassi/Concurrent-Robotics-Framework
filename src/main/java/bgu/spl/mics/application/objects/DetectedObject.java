package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

    // TODO: Define fields and methods.
    private String id;

    public String getDescription() {
        return description;
    }

    private String description;
    public DetectedObject(String id, String description){
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }
}
