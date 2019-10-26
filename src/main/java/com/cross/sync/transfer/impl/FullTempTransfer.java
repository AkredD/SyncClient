package com.cross.sync.transfer.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.transfer.Transfer;
import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;

public class FullTempTransfer extends Transfer {
    private LinuxProvider readProvider;
    private LinuxProvider writeProvider;
    private String readPath;
    private String writePath;

    public FullTempTransfer(LinuxProvider readProvider, String readPath, LinuxProvider writeProvider, String writePath) {
        this.readProvider = readProvider;
        this.writeProvider = writeProvider;
        this.readPath = readPath;
        this.writePath = writePath;
    }

    @Override
    public void run() {
        super.run = true;
        transferData();
        super.run = false;
    }

    private void transferData() {
        try {
            if (writeProvider.existFile(writePath)
                    && readProvider.getMD5FileHash(readPath).equals(writeProvider.getMD5FileHash(writePath))) {
                Slf4fLogger.info(this, String.format("File from %s by path '%s' already synchronized with %s '%s'"
                        , readProvider.getClass().getSimpleName(), readPath
                        , writeProvider.getClass().getSimpleName(), writePath));
                return;
            }
            String writeTempPath = writePath + ".temp";
            writeProvider.createFile(writeTempPath);
            source = readProvider.loadFile(readPath);
            destination = writeProvider.uploadFile(writeTempPath);
            super.transferTo();
            source.close();
            destination.close();
            if (Thread.currentThread().isInterrupted()) {
                writeProvider.deleteFile(writeTempPath);
                Slf4fLogger.info(this, String.format("Synchronizing file from %s by path '%s'  with %s '%s' was interrupted"
                        , readProvider.getClass().getSimpleName(), readPath
                        , writeProvider.getClass().getSimpleName(), writePath));
                return;
            }
            writeProvider.moveFile(writeTempPath, writePath);
            Slf4fLogger.info(this, String.format("File from %s by path '%s' synchronized with %s '%s'"
                    , readProvider.getClass().getSimpleName(), readPath
                    , writeProvider.getClass().getSimpleName(), writePath));
        } catch (ProviderException | IOException e) {
            Slf4fLogger.error(this, e.getMessage());
        }
    }
}
