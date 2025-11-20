package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private final String source;

    public TerminatedBroadcast(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "TerminatedBroadcast{" +
                "source='" + source + '\'' +
                '}';
    }
}
