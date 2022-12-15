package com.solace.demo;

import java.util.concurrent.CountDownLatch;

public class CtrlCHelper {
    static CountDownLatch doneSignal = new CountDownLatch(1);
    public static void waitForCtrlC(String prompt) {
        System.out.println(prompt);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /** This handler will be called on Control-C pressed */
            @Override
            public void run() {
                doneSignal.countDown();
            }
        });
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            System.err.printf("Interrupted: %s%n", e);
        }
    }
}
