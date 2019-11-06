package com.cross.sync.transfer.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FullTempTransferTest {
    private SSHProvider remoteProvider;
    private LocalProvider localProvider;
    private String fromPath;
    private String toPath;

    @BeforeEach
    void setUp() {
        remoteProvider = new SSHProvider("", "localhost", System.getProperty("user.name"));
        localProvider = new LocalProvider("");
        try {
            remoteProvider.open();
            remoteProvider.ping();
        } catch (ProviderException e) {
            e.printStackTrace();
            fail();
        }
        fromPath = System.getProperty("user.home") + "/testFile.temp";
        toPath = System.getProperty("user.home") + "/testDistTest.temp";
    }

    @AfterEach
    void tearDown() {
        remoteProvider.close();
    }

    @Test
    void run() {
        try {
            File testFile = new File(fromPath);
            assertTrue(testFile.createNewFile());
            FileOutputStream out = new FileOutputStream(testFile);
            byte[] bytes = {1, 3, 1, 6, 7, 1};
            out.write(bytes);
            out.close();
            Runnable transferJob = new FullTempTransfer("test", localProvider, fromPath, remoteProvider, toPath);
            Thread thread = new Thread(transferJob);
            thread.start();
            thread.join();
            localProvider.deleteFile(fromPath);
            remoteProvider.deleteFile(toPath);
        } catch (IOException | ProviderException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }
}