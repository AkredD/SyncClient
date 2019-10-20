package com.cross.sync.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public abstract class Transfer implements Callable {
    InputStream source;
    OutputStream destination;
    private Integer status;
    Transfer(InputStream source, OutputStream destination) {
        this.source = source;
        this.destination = destination;
    }

    public Object call() throws Exception {
        transferTo();
        return true;
    }

    protected abstract void transferTo() throws IOException;

    public Integer getStatus() {
        return status;
    }
}
