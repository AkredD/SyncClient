package com.cross.sync.transfer;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.transfer.impl.FullTempTransfer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

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
            remoteProvider = new SSHProvider("", "localhost", System.getProperty("user.name"));
            remoteProvider.open();
            localProvider = new LocalProvider("");
        } catch (ProviderException e) {
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
        remoteProvider.close();
    }

    @Test
    void addForScheduling() {
        try {
            localProvider.uploadFile(fromPathFirst).write(new byte[20000000]);
            localProvider.uploadFile(fromPathSecond).write(new byte[30000000]);
            FullTempTransfer transferFirst = new FullTempTransfer("test1", localProvider, fromPathFirst, remoteProvider, toPathFirst);
            FullTempTransfer transferSecond = new FullTempTransfer("test2", localProvider, fromPathSecond, remoteProvider, toPathSecond);
            TransferScheduler.getInstance().addForScheduling(transferFirst);
            TransferScheduler.getInstance().addForScheduling(transferSecond);
            Thread.sleep(15000);
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