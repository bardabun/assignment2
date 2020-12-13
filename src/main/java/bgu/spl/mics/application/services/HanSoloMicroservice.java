package bgu.spl.mics.application.services;


import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Ewok;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Map<Ewok, Boolean> ewoksAvailability = new ConcurrentHashMap<>();

    public HanSoloMicroservice() {
        super("Han");
    }

    public void acquireEwoks(int size) {

    }

    @Override
    protected void initialize() {

        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        try {
            messageBus.register(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        subscribeEvent(AttackEvent.class, callback -> {
        List<Integer> ewoksSerialsNum = callback.getAttack().getSerials();
        //ewoksAvailability.acquireEwoks(ewoksSerialsNum.size());

    });
         */
    }
}
