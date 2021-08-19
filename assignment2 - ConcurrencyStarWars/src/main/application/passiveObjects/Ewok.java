package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {

    int serialNumber;
	boolean available;

	public Ewok() { available = true; }

	public Ewok(int i) {
	    serialNumber = i;
	    available = true;
    }

    /**
     * Acquires an Ewok
     */

    public synchronized void acquire() { available = false; }

    /**
     * release an Ewok
     */

    public synchronized void release() {
    	available = true;
    }

}
