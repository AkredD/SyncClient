package com.cross.sync.transfer;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.transfer.impl.FullTempTransfer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TransferSchedulerTest {
    private final String home = System.getProperty("user.home") + "/";
    private SSHProvider remoteProvider;
    private LocalProvider localProvider;
    private String fromPathFirst;
    private String toPathFirst;
    private String fromPathSecond;
    private String toPathSecond;

    @BeforeEach
    void setUp() {
        try {
            remoteProvider = new SSHProvider("localhost", System.getProperty("user.name"));
            remoteProvider.open();
            localProvider = new LocalProvider();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        fromPathFirst = home + "test1.from";
        toPathFirst = home + "test1.to";
        fromPathSecond = home + "test2.from";
        toPathSecond = home + "test2.to";
    }

    @AfterEach
    void tearDown() {
        try {
            remoteProvider.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void addForScheduling() {
        try {
            localProvider.uploadFile(fromPathFirst).write(new byte[20000000]);
            localProvider.uploadFile(fromPathSecond).write(new byte[30000000]);
            FullTempTransfer transferFirst = new FullTempTransfer(localProvider, fromPathFirst, remoteProvider, toPathFirst);
            FullTempTransfer transferSecond = new FullTempTransfer(localProvider, fromPathSecond, remoteProvider, toPathSecond);
            TransferScheduler.getInstance().addForScheduling(transferFirst);
            TransferScheduler.getInstance().addForScheduling(transferSecond);
            Thread.sleep(15000);
            assertEquals(localProvider.getMD5FileHash(fromPathFirst), remoteProvider.getMD5FileHash(toPathFirst));
            assertEquals(localProvider.getMD5FileHash(fromPathSecond), remoteProvider.getMD5FileHash(toPathSecond));
            localProvider.deleteFile(fromPathFirst);
            localProvider.deleteFile(fromPathSecond);
            remoteProvider.deleteFile(toPathFirst);
            remoteProvider.deleteFile(toPathSecond);
            TransferScheduler.getInstance().deleteFromScheduling(transferFirst);
            TransferScheduler.getInstance().deleteFromScheduling(transferSecond);
            assertFalse(transferFirst.isRun());
            assertFalse(transferSecond.isRun());
        } catch (ProviderException | IOException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }

    }
}