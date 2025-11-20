package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private LinkedList<StampedCloudPoints> cloudPoints = new LinkedList<>();
    public boolean isLastInput(StampedCloudPoints check){
        return check.equals(cloudPoints.getLast());
    }
    public LinkedList<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    private static class LiDarSingleHolder {
        private static LiDarDataBase instance = new LiDarDataBase();
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        // TODO: Implement this
        LiDarSingleHolder.instance.loadData(filePath);
        return LiDarSingleHolder.instance;
    }

    private void loadData(String filePath) {
        if (cloudPoints.isEmpty()) {
            try (FileReader reader = new FileReader(filePath)) {

                JsonArray dataArray = JsonParser.parseReader(reader).getAsJsonArray();
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject entry = dataArray.get(i).getAsJsonObject();

                    int time = entry.get("time").getAsInt();

                    String id = entry.get("id").getAsString();


                    JsonArray cloudPointsArray = entry.getAsJsonArray("cloudPoints");
                    LinkedList<Double[]> cloudPointsList = new LinkedList<>();
                    for (int j = 0; j < cloudPointsArray.size(); j++) {
                        JsonArray pointArray = cloudPointsArray.get(j).getAsJsonArray();
                        Double[] array = new Double[3];
                        array[0] = pointArray.get(0).getAsDouble();
                        array[1] = pointArray.get(1).getAsDouble();
                        array[2] = pointArray.get(2).getAsDouble();
                        cloudPointsList.add(array);
                    }

                    StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(id, time, cloudPointsList);
                    cloudPoints.add(stampedCloudPoints);
                }
            } catch (IOException e) {
                System.err.println("Error reading JSON file: " + e.getMessage());
            }
        }
    }

    public static LiDarDataBase getinstance() {
        return LiDarSingleHolder.instance;
    }

    public void addCloudPoint(StampedCloudPoints toAdd) {
        cloudPoints.add(toAdd);
    }

    public StampedCloudPoints getCoordinates(String id, int time) {
        for (StampedCloudPoints toReturn : cloudPoints) {
            if (toReturn.getId().equals("ERROR")) {
                return toReturn;
            }
            if (toReturn.getTime() == time && toReturn.getId().equals(id)) {
                return toReturn;
            }
        }
        return null;
    }
}
