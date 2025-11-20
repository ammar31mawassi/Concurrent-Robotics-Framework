package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {

    // TODO: Define fields and methods.
    private int currTick;
    private STATUS status;

    public void setCurrTick(int currTick) {
        this.currTick = currTick;
        if(currTick == poseList.size() + 1){
            this.status = STATUS.DOWN;
        }
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public ArrayList<Pose> getPoseList() {
        return poseList;
    }

    public int getCurrTick() {
        return currTick;
    }

    public STATUS getStatus() {
        return status;
    }

    public void addPose(Pose pose){
        poseList.add(pose);
    }

    public Pose getPoseNow(){
        if(poseList.isEmpty() || currTick == 0){
            return null;
        }
        return poseList.get(currTick - 1);
    }
    private ArrayList<Pose> poseList;
    public GPSIMU(int currTick,String dataPath) {
        this.currTick = currTick;
        this.status= STATUS.UP;
        this.poseList = new ArrayList<>();
        try(FileReader read=new FileReader(dataPath)) {

            JsonArray dataArray=JsonParser.parseReader(read).getAsJsonArray();
            for(int i=0; i<dataArray.size();i++) {
                JsonObject entry=dataArray.get(i).getAsJsonObject();

                int time=entry.get("time").getAsInt();
                float x=entry.get("x").getAsFloat();
                float y=entry.get("y").getAsFloat();
                float yaw=entry.get("yaw").getAsFloat();

                Pose pose=new Pose(x,y,yaw,time);
                this.poseList.add(pose);
            }
        } catch(IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }
}
