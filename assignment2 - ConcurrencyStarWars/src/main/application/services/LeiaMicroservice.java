package bgu.spl.mics.application.services;

import java.util.List;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.FinishedAttackBroadcast;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends DiaryKeepingMicroService {

    private Attack[] attacks;
    private int finishedAttacks;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		finishedAttacks = 0;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(FinishedAttackBroadcast.class, (c) ->
        {
            finishedAttacks++;
            if (finishedAttacks == attacks.length) {// if all attacks have been finished
                sendEvent(new DeactivationEvent());
            }
        });
        subscribeBroadcast(TerminateMissionBroadcast.class, (c) ->
        {
            terminate();
            diary.setTerminateTimestamp(this);
        });

        try { Main.countDownLatch.await(); }
        catch (InterruptedException x) {}

        for (Attack attack : attacks) {   // create all attack events and send them
            long duration = attack.getDuration();
            List<Integer> serialsList = attack.getSerials();
            serialsList.sort((o1, o2) -> o1 - o2); // sort the serials to avoid deadlock
            Integer[] serialsArray = new Integer[serialsList.size()];
            serialsArray = serialsList.toArray(serialsArray);
            AttackEvent attackEvent = new AttackEvent(serialsArray, duration);
            sendEvent(attackEvent);
        }
    }
}
