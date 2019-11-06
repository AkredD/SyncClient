package com.cross.sync.transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransferScheduler {
    private static TransferScheduler instance;
    private final Map<Transfer, ScheduledFuture> runningTransfers;
    private final ScheduledThreadPoolExecutor executorService;

    private TransferScheduler() {
        this.runningTransfers = new HashMap<>();
        int THREAD_POOL_SIZE = 10;
        this.executorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        this.executorService.setRemoveOnCancelPolicy(true);
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

    public synchronized void addForScheduling(Transfer transfer) {
        if (!runningTransfers.containsKey(transfer)) {
            long TIMER_PERIOD = 60000L;
            ScheduledFuture scheduledFuture = executorService.scheduleWithFixedDelay(transfer, 0, TIMER_PERIOD, TimeUnit.MILLISECONDS);
            runningTransfers.put(transfer, scheduledFuture);
        }
    }

    public synchronized void deleteFromScheduling(Transfer transfer) {
        if (runningTransfers.containsKey(transfer)) {
            transfer.interrupt();
            transfer.status = 0;
            runningTransfers.get(transfer).cancel(false);
            runningTransfers.remove(transfer);
        }
    }

    public boolean isTransferScheduling(Transfer transfer) {
        return runningTransfers.containsKey(transfer);
    }


    public void exit() {
        executorService.shutdown();
    }
}
