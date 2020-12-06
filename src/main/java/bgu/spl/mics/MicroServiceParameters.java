package bgu.spl.mics;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The MicroServiceParameters is a class that holds all the parameters describe each
 * microService in the Message Bus.
 *
 */
public class MicroServiceParameters {
    private String microServiceName;
    private final Queue<Message> q = new LinkedList<>();
    private Object eventType;
    private Object broadcastType;

    public MicroServiceParameters(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    public String getMicroServiceName() {
        return microServiceName;
    }

    public void setMicroServiceName(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    public Queue<Message> getQ() {
        return q;
    }

    public Object getEventType() {
        return eventType;
    }

    public void setEventType(Object eventType) {
        this.eventType = eventType;
    }

    public Object getBroadcastType() {
        return broadcastType;
    }

    public void setBroadcastType(Object broadcastType) {
        this.broadcastType = broadcastType;
    }
}
