package com.cross.sync.util;

import net.schmizz.sshj.sftp.RemoteFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class RemoteInputStream extends ByteArrayInputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final RemoteFile rf;
    private int absoluteBlockPosition = 0;

    public RemoteInputStream(final RemoteFile rf) throws IOException {
        super(new byte[0]);
        this.rf = rf;
        super.count = (int) rf.fetchAttributes().getSize();
        super.pos = 0;
    }

    @Override
    public int read() {
        try {
            if (!(super.pos >= absoluteBlockPosition * DEFAULT_BUFFER_SIZE && super.pos < (absoluteBlockPosition + 1) * DEFAULT_BUFFER_SIZE)) {
                absoluteBlockPosition = super.pos / DEFAULT_BUFFER_SIZE;
                byte[] localBuf = new byte[DEFAULT_BUFFER_SIZE];
                rf.read(absoluteBlockPosition * DEFAULT_BUFFER_SIZE, localBuf, absoluteBlockPosition * DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
                super.buf = localBuf;
            }
            return this.pos < this.count ? this.buf[this.pos++ - absoluteBlockPosition * DEFAULT_BUFFER_SIZE] & 255 : -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) {
        try {
            Objects.checkFromIndexSize(off, len, b.length);
            if (this.pos >= this.count) {
                return -1;
            } else {
                int avail = this.count - this.pos;
                if (len > avail) {
                    len = avail;
                }
                if (len <= 0) {
                    return 0;
                } else {
                    rf.read(super.pos, b, off, len);
                    this.pos += len;
                    return len;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized byte[] readAllBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized long transferTo(OutputStream out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized int available() {
        try {
            return (int) rf.fetchAttributes().getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        rf.close();
        super.close();
    }
}
