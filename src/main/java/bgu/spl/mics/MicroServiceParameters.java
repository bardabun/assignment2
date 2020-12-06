package bgu.spl.mics;

import java.util.Queue;

/**
 * The MicroServiceParameters is a class that holds all the parameters describe each
 * microService in the Message Bus.
 *
 */
public class MicroServiceParameters {
    private String microServiceName;
    private Queue q;
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

    public Queue getQ() {
        return q;
    }

    public void setQ(Queue q) {
        this.q = q;
    }

    public Message getMessageType() {
        return messageType;
    }

    public void setMessageType(Message messageType) {
        this.messageType = messageType;
    }
}
