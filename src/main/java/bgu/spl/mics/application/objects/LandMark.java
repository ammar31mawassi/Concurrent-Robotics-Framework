package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    // TODO: Define fields and methods.
    private String id;
    private String description;
    private LinkedList<CloudPoint> coordinates;

    public String getId() {
        return id;
    }

    public LinkedList<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public LandMark(String id, String description, LinkedList<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }
}
