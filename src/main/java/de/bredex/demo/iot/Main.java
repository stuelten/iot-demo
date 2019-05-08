package de.bredex.demo.iot;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {
    public static final int SENSORS = 22;
    private static final int DURATION_DEFAULT_IN_SECONDS = 10;
    private static final Object[] LOCK = {};

    public static void sleep(int timeoutMillis) {
        try {
            synchronized (LOCK) {
                LOCK.wait(timeoutMillis);
            }
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

    public static void main(String[] args) {
        int durationInSeconds = args.length > 0 ? Integer.parseInt(args[0]) :
                DURATION_DEFAULT_IN_SECONDS;
        assert durationInSeconds > 0;

        Thread consumerThread = new Thread(new DemoTimeConsumer());
        consumerThread.setPriority(Thread.MAX_PRIORITY);
        consumerThread.start();

        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        };
        Executor executorSensors = Executors.newFixedThreadPool(SENSORS);
        for (int i = 0; i < SENSORS; i++) {
            executorSensors.execute(new DemoTimeSensor());
            sleep(1000 / SENSORS);
        }

        sleep(durationInSeconds * 1000);

        System.out.println("Send: " + DemoTimeSensor.msgSend + ", received: " + DemoTimeConsumer.msgReceived);

        System.exit(0);
    }

}