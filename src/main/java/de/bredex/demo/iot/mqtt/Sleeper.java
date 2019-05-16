package de.bredex.demo.iot.mqtt;

public class Sleeper {

    /**
     * Sleeps the given time.
     *
     * @param timeoutMillis time to wait in millis
     * @return {@code true} if the caller thread was interrupted.
     */
    public static boolean sleep(int timeoutMillis) {
        boolean ret = false;
        try {
            Thread.currentThread().wait();
        } catch (InterruptedException ie) {
            ret = true;
        }
        return ret;
    }

}
