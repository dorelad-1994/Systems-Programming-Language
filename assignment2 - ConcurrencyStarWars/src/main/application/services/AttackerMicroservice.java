package bgu.spl.mics.application.services;


import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishedAttackBroadcast;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public abstract class AttackerMicroservice extends DiaryKeepingMicroService {

    /* Abstract Class that HanSoloMicroservice and C3POMicroservice extend. They are basically the same. */
    public AttackerMicroservice(String name) {
        super(name);
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (e) ->
        {
            Ewoks ewoks = Ewoks.getInstance();
            Integer[] serials = e.getSerials();
            for (int i = 0; i < serials.length; i++) {
                Ewok ewok = ewoks.getEwok(serials[i]);
                ewok.acquire();
            }
            try {
                Thread.sleep(e.getDuration());// simulate attack
            } catch (InterruptedException x) {
            }
            for (int i = 0; i < serials.length; i++) {
                Ewok ewok = ewoks.getEwok(serials[i]);
                ewok.release();
            }
            complete(e, true);
            sendBroadcast(new FinishedAttackBroadcast());
            diary.setAttackerFinish(this, System.currentTimeMillis());
            diary.increaseTotalAttacks();
        });

        subscribeBroadcast(TerminateMissionBroadcast.class, (c) ->
        {
            terminate();
            diary.setTerminateTimestamp(this);

        });
    }


}
