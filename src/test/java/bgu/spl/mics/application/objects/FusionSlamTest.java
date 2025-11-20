package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FusionSlamTest {
    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
    }

    @AfterEach
    void tearDown() {
        fusionSlam = null;
    }

    @Test
    void getLandmarks() {
        ArrayList<LandMark> landmarks = fusionSlam.getLandmarks();
        assertNotNull(landmarks, "Landmarks list should not be null.");
        assertEquals(0, landmarks.size(), "Landmarks list should initially be empty.");
    }

    @Test
    void getInstance() {
        FusionSlam instance1 = FusionSlam.getInstance();
        FusionSlam instance2 = FusionSlam.getInstance();
        assertNotNull(instance1, "Singleton instance should not be null.");
        assertSame(instance1, instance2, "getInstance() should always return the same instance.");
    }

    @Test
    void setStatisticalFolder() {
        StatisticalFolder stats = new StatisticalFolder();
        fusionSlam.setStatisticalFolder(stats);
        assertEquals(stats, fusionSlam.stats, "Statistical folder should be set correctly.");
    }

    @Test
    void isPoseAvailable() {
        Pose pose2 = new Pose(13, 5, 30, 2);
        fusionSlam.addPose(pose2);

        assertEquals(pose2, fusionSlam.isPoseAvailable(2), "Pose at time 2 should match.");
        assertNull(fusionSlam.isPoseAvailable(3), "Pose at unavailable time should be null.");
    }

    @Test
    void addPose() {
        Pose pose = new Pose(10, 1, 94, 1);
        fusionSlam.addPose(pose);

        Pose retrievedPose = fusionSlam.isPoseAvailable(1);
        assertNotNull(retrievedPose, "Pose should be added and retrievable.");
        assertEquals(pose, retrievedPose, "Retrieved pose should match added pose.");
    }

    @Test
    void addLandMark() {
        LinkedList<CloudPoint> coords = new LinkedList<>();
        coords.add(new CloudPoint(1.0, 1.0));
        coords.add(new CloudPoint(2.0, 2.0));
        fusionSlam.stats= new StatisticalFolder();
        fusionSlam.addLandMark("LM1", "Landmark 1", coords);

        ArrayList<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(1, landmarks.size(), "Landmarks list should contain one landmark.");
        assertEquals("LM1", landmarks.get(0).getId(), "Landmark ID should match.");
        assertEquals("Landmark 1", landmarks.get(0).getDescription(), "Landmark description should match.");

    }
}
