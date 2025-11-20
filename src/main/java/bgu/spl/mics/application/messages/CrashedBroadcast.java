package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;


public class CrashedBroadcast implements Broadcast {
    private final String source;
    private final String errorMessage;

    public CrashedBroadcast(String source, String errorMessage) {
        this.source = source;
        this.errorMessage = errorMessage;
    }

    public String getSource() {
        return source;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "CrashedBroadcast{" +
                "source='" + source + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}