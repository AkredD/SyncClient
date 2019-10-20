package com.cross.sync.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BaseTransfer extends Transfer {
    public BaseTransfer(InputStream source, OutputStream destination) {
        super(source, destination);
    }

    protected void transferTo() throws IOException {
        source.transferTo(destination);
    }
}
