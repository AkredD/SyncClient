package com.cross.sync.util;

import net.schmizz.sshj.sftp.RemoteFile;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class RemoteOutputStream extends ByteArrayOutputStream implements Closeable {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final RemoteFile rf;


    public RemoteOutputStream(final RemoteFile rf) throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        this.rf = rf;
        super.count = 0;
    }

    @Override
    public synchronized void write(int b) {
        byte[] byteArray = {(byte) b};
        try {
            rf.write(count, byteArray, count, 1);
            super.count++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        try {
            Objects.checkFromIndexSize(off, len, b.length);
            rf.write(off, b, off, len);
            super.count += len;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeBytes(byte[] b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void writeTo(OutputStream out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized byte[] toByteArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        try {
            return (int) rf.fetchAttributes().getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        rf.close();
    }
}
