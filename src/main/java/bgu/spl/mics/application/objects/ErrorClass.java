package bgu.spl.mics.application.objects;

import bgu.spl.mics.Message;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorClass {
    private static class SingltonHolder {
        public static ErrorClass instance = new ErrorClass();
    }

    public static ErrorClass getInstance() {
        return SingltonHolder.instance;
    }

    private CrashedBroadcast error = null;
    private ConcurrentHashMap<String, DetectObjectsEvent> lastFramesCamera = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TrackedObjectsEvent> lastFramesLiDar = new ConcurrentHashMap<>();

    public void addLastFrameLiDar(String id, TrackedObjectsEvent lastFrame) {
        lastFramesLiDar.put(id, lastFrame);
    }

    public void addLastFrameCamera(String id, DetectObjectsEvent lastFrame) {
        lastFramesCamera.put(id, lastFrame);
    }

    public synchronized void setError(CrashedBroadcast crashed) {
        error = crashed;
    }

    public boolean didErrorHappen() {
        return error != null;
    }

    public void writeErrorToJson(String outFile) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = new JsonObject();
        json.addProperty("error", error.getErrorMessage());
        json.addProperty("faultySensor", error.getSource());
        JsonObject cameras = new JsonObject();
        for(String key : lastFramesCamera.keySet()){
            JsonArray objects = new JsonArray();
            for(DetectedObject ob : lastFramesCamera.get(key).getDetectedObjects().getDetectedObjects()){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id",ob.getId());
                jsonObject.addProperty("description",ob.getDescription());
                objects.add(jsonObject);
            }
            cameras.add(key,objects);
        }
        json.add("lastCamerasFrame",cameras);
        JsonObject lidars = new JsonObject();
        for(String key : lastFramesLiDar.keySet()){
            JsonArray objects = new JsonArray();
            for(TrackedObject ob : lastFramesLiDar.get(key).getTrackedObjects()){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id",ob.getId());
                jsonObject.addProperty("time",ob.getTime());
                JsonArray coords = new JsonArray();
                for(CloudPoint cp : ob.getCoordinates()){
                    JsonObject point = new JsonObject();
                    point.addProperty("x",cp.getX());
                    point.addProperty("y",cp.getY());
                    coords.add(point);
                }
                jsonObject.add("coordinates",coords);
                objects.add(jsonObject);
            }
            lidars.add(key,objects);
        }
        json.add("lastLiDarWorkerTrackersFrame",lidars);
        FusionSlam fusionSlam = FusionSlam.getInstance();
        json.addProperty("systemRuntime", fusionSlam.stats.getSystemRunTime());
        json.addProperty("numDetectedObjects", fusionSlam.stats.getNumDetectedObjects());
        json.addProperty("numTrackedObjects", fusionSlam.stats.getNumTrackedObjects());
        json.addProperty("numLandmarks", fusionSlam.stats.getNumLandMarks());
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

}
