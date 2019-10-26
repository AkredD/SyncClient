package com.cross.sync.transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class TransferScheduler {
    private static TransferScheduler instance;
    private final Long TIMER_PERIOD = 300000L;
    private final Map<Transfer, Timer> timers;

    private TransferScheduler() {
        this.timers = new HashMap<>();
    }

    public static TransferScheduler getInstance() {
        if (instance == null) {
            synchronized (TransferScheduler.class) {
                if (instance == null) {
                    instance = new TransferScheduler();
                }
            }
        }
        return instance;
    }

    public void addForScheduling(Transfer transfer) {
        if (!timers.containsKey(transfer)) {
            Timer timer = new Timer();
            timer.schedule(transfer, 0, TIMER_PERIOD);
            timers.put(transfer, timer);
        }
    }

    public void deleteFromScheduling(Transfer transfer) {
        if (timers.containsKey(transfer)) {
            timers.get(transfer).cancel();
            timers.get(transfer).purge();
            timers.remove(transfer);
        }
    }
}
