package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        StatisticalFolder stats = new StatisticalFolder();
        int activeSensors = 0;

        FusionSlam fusionSlam = FusionSlam.getInstance();
        FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);
        fusionSlamService.setStatisticalFolder(stats);

        TimeService timeService = null;

        List<MicroService> services = new LinkedList<>();

        String configFilePath = args[0];
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(configFilePath)) {

            JSONInput config = gson.fromJson(reader, JSONInput.class);

            for (JSONInput.CameraConfig cameraConfig : config.getCameras().getCamerasConfigurations()) {
                Camera camera = new Camera(cameraConfig.getId(), cameraConfig.getFrequency());
                String key = cameraConfig.getCameraKey();
                String path = config.getCameras().getCameraDatasPath();
                try (FileReader readerCamera = new FileReader(path)) {

                    JsonObject root = JsonParser.parseReader(readerCamera).getAsJsonObject();
                    JsonArray cameraData = root.getAsJsonArray(key);

                    for (int i = 0; i < cameraData.size(); i++) {

                        JsonObject entry = cameraData.get(i).getAsJsonObject();
                        int time = entry.get("time").getAsInt();

                        LinkedList<DetectedObject> objects = new LinkedList<>();
                        JsonArray detectedObjects = entry.getAsJsonArray("detectedObjects");
                        for (int j = 0; j < detectedObjects.size(); j++) {

                            JsonObject detectedObject = detectedObjects.get(j).getAsJsonObject();
                            String objectId = detectedObject.get("id").getAsString();
                            String description = detectedObject.get("description").getAsString();

                            objects.add(new DetectedObject(objectId, description));
                        }

                        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(time, objects);
                        camera.addDetectedObjects(stampedDetectedObjects);
                    }

                    CameraService service = new CameraService(camera);

                    services.add(service);

                    activeSensors++;
                }
            }

            LiDarDataBase dataBase = LiDarDataBase.getInstance(config.getLiDarWorkers().getLidarsDataPath());
            for (JSONInput.LidarConfig lidarConfig : config.getLiDarWorkers().getLidarConfigurations()) {

                LiDarWorkerTracker lidar = new LiDarWorkerTracker(lidarConfig.getId(), lidarConfig.getFrequency());
                LiDarService service = new LiDarService(lidar);
                services.add(service);

                activeSensors++;
            }

            GPSIMU gpsimu = new GPSIMU(0, config.getPoseJsonFile());
            PoseService service = new PoseService(gpsimu);
            services.add(service);
            activeSensors++;

            timeService = new TimeService(config.getTickTime(), config.getDuration());


        } catch (IOException e) {
            e.printStackTrace();
        }

        fusionSlamService.setActiveSensors(activeSensors);

        Thread fusionThread = new Thread(fusionSlamService);
        fusionThread.start();
        for (MicroService service : services) {
            Thread thread = new Thread(service);
            thread.start();
        }

        Thread timeThread = new Thread(timeService);
        timeThread.start();
        try {
            fusionThread.join();
            timeThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (ErrorClass.getInstance().didErrorHappen()) {
            ErrorClass.getInstance().writeErrorToJson("output.json");
        } else {
            stats.outputFinishFile("output.json");
        }
    }

}

