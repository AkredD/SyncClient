package com.cross.sync;


import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.swing.JSync;
import com.cross.sync.swing.controller.ResourceController;
import com.cross.sync.transfer.TransferScheduler;
import com.cross.sync.transfer.impl.FullTempTransfer;

import java.io.IOException;
import java.util.HashSet;

public class SynchronizationClient {
    private final String home = System.getProperty("user.home") + "/";
    //private final String remoteHome = "/Users/macbook/";
    private final String remoteHome = System.getProperty("user.home") + "/";
    private SSHProvider remoteProvider;
    private LocalProvider localProvider;
    private String fromPathFirst;
    private String toPathFirst;
    private String fromPathSecond;
    private String toPathSecond;

    public static void main(String... args) throws ProviderException, IOException, InterruptedException {
        //(new SynchronizationClient()).run();
        ResourceController.getInstance().getLinuxProviderMap().put("local", new LocalProvider());
        ResourceController.getInstance().getTransfersByProvider().put("local", new HashSet<>());
        com.cross.sync.swing.JSync dialog = new JSync();
        dialog.pack();
        dialog.setVisible(true);
    }

    void setUp() {
        try {
            //remoteProvider = new SSHProvider("192.168.0.103", "macbook");
            remoteProvider = new SSHProvider("localhost", "akredd");
            remoteProvider.open();
            localProvider = new LocalProvider();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fromPathFirst = home + "test1.from";
        toPathFirst = remoteHome + "test1.to";
        fromPathSecond = home + "test2.from";
        toPathSecond = remoteHome + "test2.to";
    }

    void tearDown() {
        try {
            remoteProvider.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void run() {
        setUp();
        try {
            localProvider.uploadFile(fromPathFirst).write(new byte[2000000]);
            localProvider.uploadFile(fromPathSecond).write(new byte[3000000]);
            FullTempTransfer transferFirst = new FullTempTransfer(localProvider, fromPathFirst, remoteProvider, toPathFirst);
            FullTempTransfer transferSecond = new FullTempTransfer(localProvider, fromPathSecond, remoteProvider, toPathSecond);
            TransferScheduler.getInstance().addForScheduling(transferFirst);
            TransferScheduler.getInstance().addForScheduling(transferSecond);
            int i = 0;
            Thread.sleep(500);
            TransferScheduler.getInstance().deleteFromScheduling(transferFirst);
            TransferScheduler.getInstance().addForScheduling(transferFirst);
            while (transferFirst.getStatus() == null || transferSecond.getStatus() == null
                    || !(transferFirst.getStatus().equals(transferSecond.getStatus()) && transferFirst.getStatus() == 100)) {
                System.out.println("firstTransfering - " + transferFirst.getStatus() + " transferSecond " + transferSecond.getStatus());
                Thread.sleep(100);
                i++;
            }
            //assertEquals(localProvider.getMD5FileHash(fromPathFirst), remoteProvider.getMD5FileHash(toPathFirst));
            //assertEquals(localProvider.getMD5FileHash(fromPathSecond), remoteProvider.getMD5FileHash(toPathSecond));
            localProvider.deleteFile(fromPathFirst);
            localProvider.deleteFile(fromPathSecond);
            remoteProvider.deleteFile(toPathFirst);
            remoteProvider.deleteFile(toPathSecond);
            TransferScheduler.getInstance().deleteFromScheduling(transferFirst);
            TransferScheduler.getInstance().deleteFromScheduling(transferSecond);
            //fail();
        } catch (ProviderException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        tearDown();
    }
}
