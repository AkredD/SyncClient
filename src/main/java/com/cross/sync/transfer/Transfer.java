package com.cross.sync.transfer;

import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;

public abstract class Transfer extends TimerTask {
    protected InputStream source;
    protected OutputStream destination;
    protected volatile StringBuilder log;
    protected volatile Integer status = 0;
    private volatile boolean run = false;
    private volatile boolean interrupted;

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
            byte[] buffer = new byte[1024];
            int len;
            while ((len = source.read(buffer)) != -1) {
                destination.write(buffer, 0, len);
                transferedSize += len;
                if (interrupted) {
                    return;
                }
            }
        } catch (IOException e) {
            Slf4fLogger.error(this, e.getMessage());
        }
    }

    public String getLog() {
        return log.toString();
    }

    public Integer getStatus() {
        return status;
    }

    public boolean isRun() {
        return run;
    }

    void interrupt() {
        this.interrupted = true;
    }

    protected boolean isInterrupted() {
        return interrupted;
    }
}
