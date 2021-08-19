package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.AttackEvent;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends AttackerMicroservice {

    public HanSoloMicroservice() {
        super("Han");
    }

}
