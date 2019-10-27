package com.cross.sync.transfer;

import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;

public abstract class Transfer extends TimerTask {
    protected InputStream source;
    protected OutputStream destination;
    protected volatile boolean run = false;
    protected volatile boolean interrupted;
    private volatile Integer status;

    public void run() {
        run = true;
        interrupted = false;
        transferData();
        interrupted = false;
        run = false;
    }

    protected abstract void transferData();

    protected void transferTo() {
        try {
            int transferedSize = 0;
            int size = source.available();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = source.read(buffer)) != -1) {
                destination.write(buffer, transferedSize, len);
                transferedSize += len;
                status = (int) ((((long) transferedSize) * 100L) / ((long) size));
                if (interrupted) {
                    return;
                }
            }
        } catch (IOException e) {
            Slf4fLogger.error(this, e.getMessage());
        }
    }

    public Integer getStatus() {
        return status;
    }

    public boolean isRun() {
        return run;
    }

    public void interrupt() {
        this.interrupted = true;
    }

    protected boolean isInterrupted() {
        return interrupted;
    }
}
