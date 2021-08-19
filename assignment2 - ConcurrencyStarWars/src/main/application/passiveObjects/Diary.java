package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private static class DiarySingletonHolder {
        private static Diary instance = new Diary();
    }

    private Diary() {
        totalAttacks = new AtomicInteger(0);
    }

    public static Diary getInstance() {
        return DiarySingletonHolder.instance;
    }

    public void setTerminateTimestamp(MicroService m) {
        if (m.getClass() == HanSoloMicroservice.class) {
            HanSoloTerminate = System.currentTimeMillis();
        } else if (m.getClass() == C3POMicroservice.class) {
            C3POTerminate = System.currentTimeMillis();
        } else if (m.getClass() == R2D2Microservice.class) {
            R2D2Terminate = System.currentTimeMillis();
        } else if (m.getClass() == LandoMicroservice.class) {
            LandoTerminate = System.currentTimeMillis();
        } else if (m.getClass() == LeiaMicroservice.class) {
            LeiaTerminate = System.currentTimeMillis();
        }
    }

    public void increaseTotalAttacks() {
        int old;
        do {
            old = totalAttacks.get();
        } while (!totalAttacks.compareAndSet(old, old + 1));
    }

    public void setAttackerFinish (MicroService m, long t) {
        if (m.getClass() == HanSoloMicroservice.class) {
            HanSoloFinish = t;
        } else if (m.getClass() == C3POMicroservice.class) {
            C3POFinish = t;
        }
    }

    public void setR2D2Deactivate(long t) {
        R2D2Deactivate = t;
    }

    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

}
