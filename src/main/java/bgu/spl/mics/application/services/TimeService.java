package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private int tickTime; // Duration of each tick in milliseconds
    private int duration; // Total number of ticks
    private int currentTick;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = TickTime * 1000;
        this.duration = Duration;
        this.currentTick = 1;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
        try{
            Thread.sleep(1000);
        }
        catch(InterruptedException e){

        }
        while (currentTick <= duration) {
            sendBroadcast(new TickBroadcast(currentTick));
            try {
                Thread.sleep(tickTime);
            } catch (InterruptedException e) {
            }
            currentTick++;
        }
        sendBroadcast(new TerminatedBroadcast(this.getName()));
        terminate();
    }
}
