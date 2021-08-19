package bgu.spl.mics.application.passiveObjects;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private static class EwoksSingletonHolder {
        private static Ewoks instance = new Ewoks();
    }

    private Ewok[] ewoks;

    private Ewoks () {
        ewoks = new Ewok[0];
    }

    public static Ewoks getInstance() {
        return EwoksSingletonHolder.instance;
    }

    public Ewok getEwok (int i) {
        return ewoks[i];
    }

    // Will be used only at the beginning to initiate Ewoks, but for correctness purposes will work if called at any time
    public void addEwoks (int num) {
        int size = ewoks.length + num + 1;
        Ewok[] ewoks2 = new Ewok[size];
        for (int i = 0; i < size; i++) {
            if (i < ewoks.length) {
                ewoks2[i] = ewoks[i];
            } else {
              ewoks2[i] = new Ewok(i);
            }
        }
        ewoks = ewoks2;
    }
}
