package bgu.spl.mics;

public class AttackMicroService extends MicroService {
    private Message messageType;
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public AttackMicroService(String name) {
        super(name);
    }

    public Message getMessageType(){
        return messageType;
    }

    @Override
    protected void initialize() {
    }


}
