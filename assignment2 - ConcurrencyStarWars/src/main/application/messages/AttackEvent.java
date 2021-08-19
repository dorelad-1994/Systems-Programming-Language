package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;


public class AttackEvent implements Event<Boolean> {

    private Integer[] serials;
    private long duration;

    public AttackEvent(Integer[] serials, long duration) {
        this.serials = serials;
        this.duration = duration;
    }

    public Integer[] getSerials() { return serials; }

    public long getDuration() { return  duration; }
}
