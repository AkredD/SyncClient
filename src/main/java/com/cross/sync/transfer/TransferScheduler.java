package com.cross.sync.transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransferScheduler {
    private static TransferScheduler instance;
    private final Long TIMER_PERIOD = 300000L;
    private final Integer THREAD_POOL_SIZE = 10;
    private final Map<Transfer, ScheduledFuture> runningTransfers;
    private final ScheduledThreadPoolExecutor executorService;

    private TransferScheduler() {
        this.runningTransfers = new HashMap<>();
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

    public void addForScheduling(Transfer transfer) {
        if (!runningTransfers.containsKey(transfer)) {
            ScheduledFuture scheduledFuture = executorService.scheduleWithFixedDelay(transfer, 0, TIMER_PERIOD, TimeUnit.MILLISECONDS);
            runningTransfers.put(transfer, scheduledFuture);
        }
    }

    public void deleteFromScheduling(Transfer transfer) {
        if (runningTransfers.containsKey(transfer)) {
            transfer.interrupt();
            runningTransfers.get(transfer).cancel(false);
        }
    }

    public boolean isTransferScheduling(Transfer transfer) {
        return runningTransfers.containsKey(transfer);
    }


}
