package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends DiaryKeepingMicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
       subscribeEvent(BombDestroyerEvent.class, (e) ->
       {
           try { Thread.sleep(duration); } // simulate bombing
           catch (InterruptedException x) {}
           complete(e, true);
           sendBroadcast(new TerminateMissionBroadcast());
       });
        subscribeBroadcast(TerminateMissionBroadcast.class, (c) ->
        {
            terminate();
            diary.setTerminateTimestamp(this);

        });
    }
}
