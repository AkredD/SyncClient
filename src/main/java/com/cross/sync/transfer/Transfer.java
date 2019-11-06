package com.cross.sync.transfer;

import com.cross.sync.provider.Provider;

import java.util.TimerTask;

public abstract class Transfer extends TimerTask {
    protected final Provider readProvider;
    protected final Provider writeProvider;
    protected final String readPath;
    protected final String writePath;
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

    /**
     * Main method for overriding transfer strategy. Using readProvider, writeProvider, readPath and writePath
     * for recursive data transferring. Make sure, you are updating relative transfer data like log and status.
     * After executing data should be transfered.
     */
    protected abstract void transferData();

    /**
     * @return name of transfer
     */
    public String getName() {
        return name;
    }

    /**
     * Set from gui only
     *
     * @param name name of transfer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Log for transfer details dialog
     *
     * @return transfer log
     */
    public String getLog() {
        return log.toString();
    }

    /**
     * Using in transfer progress bar
     *
     * @return transfer status from 0 to 100
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Returns true if transfer is executing, else otherwise
     *
     * @return run status
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Light interruption of running transfer
     */
    void interrupt() {
        this.interrupted = true;
    }

    /**
     * @return interrupted status
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * @return using read provider
     */
    public Provider getReadProvider() {
        return readProvider;
    }

    /**
     * @return using write provider
     */
    public Provider getWriteProvider() {
        return writeProvider;
    }

    /**
     * @return using read path
     */
    public String getReadPath() {
        return readPath;
    }

    /**
     * @return using write path
     */
    public String getWritePath() {
        return writePath;
    }
}
