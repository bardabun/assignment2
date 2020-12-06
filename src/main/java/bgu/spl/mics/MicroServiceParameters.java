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
    private Message messageType;

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

    public Message getMessageType() {
        return messageType;
    }

    public void setMessageType(Message messageType) {
        this.messageType = messageType;
    }
}
