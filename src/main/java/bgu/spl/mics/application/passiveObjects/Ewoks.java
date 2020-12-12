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
    private static int size;
    private Ewok[] ewoks;

    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks(size);

    }
    private Ewoks() {

    }
    public static Ewoks getInstance() {
        return SingletonHolder.instance;
    }
    private Ewoks(int size) {
        this.size = size;
    }

    public void initialize(int size) {
        ewoks = new Ewok[size + 1];
        for (int i = 0; i <= size; i++) {
            Ewok tmp = new Ewok(size);
            ewoks[i] = tmp;
        }
    }


}

