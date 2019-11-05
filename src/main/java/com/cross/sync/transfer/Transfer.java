package com.cross.sync.transfer;

import com.cross.sync.provider.Provider;
import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;

public abstract class Transfer extends TimerTask {
    protected final Provider readProvider;
    protected final Provider writeProvider;
    protected final String readPath;
    protected final String writePath;
    protected InputStream source;
    protected OutputStream destination;
    protected volatile StringBuilder log;
    protected volatile Integer status = 0;
    protected volatile boolean interrupted;
    private volatile boolean run = false;
    private volatile String name;

    protected Transfer(Provider readProvider, Provider writeProvider, String readPath, String writePath) {
        this.readProvider = readProvider;
        this.writeProvider = writeProvider;
        this.readPath = readPath;
        this.writePath = writePath;
    }

    public void run() {
        run = true;
        interrupted = false;
        transferData();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isInterrupted() {
        return interrupted;
    }

    public Provider getReadProvider() {
        return readProvider;
    }

    public Provider getWriteProvider() {
        return writeProvider;
    }

    public String getReadPath() {
        return readPath;
    }

    public String getWritePath() {
        return writePath;
    }
}
