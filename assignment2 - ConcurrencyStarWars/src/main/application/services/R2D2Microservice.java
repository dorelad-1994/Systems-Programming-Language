package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends DiaryKeepingMicroService {

    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, (e) ->
        {
            try { Thread.sleep(duration); } // simulates deactivating the shields
            catch (InterruptedException x) {}
            complete(e, true);
            diary.setR2D2Deactivate(System.currentTimeMillis());
            sendEvent(new BombDestroyerEvent());
        });

        subscribeBroadcast(TerminateMissionBroadcast.class, (c) ->
        {
            terminate();
            diary.setTerminateTimestamp(this);
        });
    }
}
