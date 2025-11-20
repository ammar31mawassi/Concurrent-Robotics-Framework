package bgu.spl.mics.application.objects;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    public AtomicInteger getSystemRunTime() {
        return systemRunTime;
    }

    public AtomicInteger getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public AtomicInteger getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public AtomicInteger getNumLandMarks() {
        return numLandMarks;
    }

    // TODO: Define fields and methods for statistics tracking.
    private AtomicInteger systemRunTime, numDetectedObjects, numTrackedObjects, numLandMarks;
    private boolean isFinalRunTime;

    public synchronized void setSystemRunTime(int systemRunTim) {
        if(this.systemRunTime.get() < systemRunTim && !isFinalRunTime) {
            this.systemRunTime.set(systemRunTim);
        }
    }
    public void isFinalCall(){
        isFinalRunTime = true;
    }
    public StatisticalFolder() {
        systemRunTime = new AtomicInteger(0);
        numDetectedObjects = new AtomicInteger(0);
        numLandMarks = new AtomicInteger(0);
        numTrackedObjects = new AtomicInteger(0);
        isFinalRunTime = false;
    }

    public void addTrackedObjectsToStats(int size) {
        numTrackedObjects.addAndGet(size);
    }

    public void addDetectedObjectsToStats(int size) {
        numDetectedObjects.addAndGet(size);
    }

    public void addLandmarkToStats() {
        numLandMarks.addAndGet(1);
    }

    public void outputFinishFile(String outFile){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = new JsonObject();
        json.addProperty("systemRuntime", systemRunTime.get());
        json.addProperty("numDetectedObjects", numDetectedObjects);
        json.addProperty("numTrackedObjects", numTrackedObjects);
        json.addProperty("numLandmarks", numLandMarks);
        FusionSlam fusionSlam = FusionSlam.getInstance();
        JsonObject landmarksJson = new JsonObject();
        for (LandMark landmark : fusionSlam.getLandmarks()) {
            JsonObject landmarkJson = new JsonObject();
            landmarkJson.addProperty("id", landmark.getId());
            landmarkJson.addProperty("description", landmark.getDescription());

            JsonArray coordinatesJson = new JsonArray();
            for (CloudPoint point : landmark.getCoordinates()) {
                JsonObject pointJson = new JsonObject();
                pointJson.addProperty("x", point.getX());
                pointJson.addProperty("y", point.getY());
                coordinatesJson.add(pointJson);
            }
            landmarkJson.add("coordinates", coordinatesJson);
            landmarksJson.add(landmark.getId(), landmarkJson);
        }

        json.add("landMarks", landmarksJson);

        try (FileWriter writer = new FileWriter(outFile)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "StatisticalFolder{" +
                "systemRunTime=" + systemRunTime +
                ", numDetectedObjects=" + numDetectedObjects +
                ", numTrackedObjects=" + numTrackedObjects +
                ", numLandMarks=" + numLandMarks +
                ", isFinalRunTime=" + isFinalRunTime +
                '}';
    }
}
