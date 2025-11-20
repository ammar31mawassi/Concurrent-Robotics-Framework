package bgu.spl.mics.application;

import java.util.List;

public class JSONInput {
    private CamerasConfig Cameras;
    private LiDarWorkersConfig LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    public CamerasConfig getCameras() {
        return Cameras;
    }

    public LiDarWorkersConfig getLiDarWorkers() {
        return LiDarWorkers;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public int getTickTime() {
        return TickTime;
    }

    public int getDuration() {
        return Duration;
    }

    static class CamerasConfig {
        private List<CameraConfig> CamerasConfigurations;
        private String camera_datas_path;

        public List<CameraConfig> getCamerasConfigurations() {
            return CamerasConfigurations;
        }

        public String getCameraDatasPath() {
            return camera_datas_path;
        }
    }

    static class CameraConfig {
        private int id;
        private int frequency;
        private String camera_key;

        public int getId() {
            return id;
        }

        public int getFrequency() {
            return frequency;
        }

        public String getCameraKey() {
            return camera_key;
        }
    }

    static class LiDarWorkersConfig {
        private List<LidarConfig> LidarConfigurations;
        private String lidars_data_path;

        public List<LidarConfig> getLidarConfigurations() {
            return LidarConfigurations;
        }

        public String getLidarsDataPath() {
            return lidars_data_path;
        }
    }

    static class LidarConfig {
        private int id;
        private int frequency;

        public int getId() {
            return id;
        }

        public int getFrequency() {
            return frequency;
        }
    }


}
