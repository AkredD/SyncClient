package com.cross.sync.transfer;

import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class TransferThread extends Thread {
    protected InputStream source;
    protected OutputStream destination;
    private Integer status;

    public abstract void run();

    protected void transferTo() {
        try {
            int transferedSize = 0;
            int size = source.available();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = source.read(buffer)) != -1) {
                destination.write(buffer, 0, len);
                transferedSize += len;
                status = (int) ((((long) transferedSize) * 100L) / ((long) size));
                if (Thread.currentThread().isInterrupted()) {
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
}
