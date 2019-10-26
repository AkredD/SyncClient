package com.cross.sync;


import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.transfer.TransferScheduler;
import com.cross.sync.transfer.impl.FullTempTransfer;

import java.io.IOException;

public class SynchronizationClient {
    private final String home = System.getProperty("user.home") + "/";
    private final String remoteHome = "/Users/macbook/";
    private SSHProvider remoteProvider;
    private LocalProvider localProvider;
    private String fromPathFirst;
    private String toPathFirst;
    private String fromPathSecond;
    private String toPathSecond;

    public static void main(String... args) throws ProviderException, IOException, InterruptedException {
        (new SynchronizationClient()).run();
        /*String host = "192.168.0.106";
        String publicKey = "macbook";
        SSHProvider provider = new SSHProvider(host, publicKey);
        provider.open();
        LocalProvider localProvider = new LocalProvider();
        provider.ping();
        provider.uploadFile("~/tempTemp.temp");
        System.out.println("remote md5 " + provider.getMD5FileHash("/home/akredd/temp/test"));
        System.out.println("remote md5 " + provider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println("local md5 " + localProvider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println("local md5 " + localProvider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println(IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toString());
        byte[] content = IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toByteArray();
        provider.uploadFile("/home/akredd/temp/empty2").write(content, 0, content.length);*/
        //FullTempTransfer tempTransfer = new FullTempTransfer(provider, "/home/akredd/temp/test", localProvider, "/home/akredd/temp/testTest");
        //tempTransfer.start();
        //tempTransfer.join();
        //provider.close();
    }

    void setUp() {
        try {
            remoteProvider = new SSHProvider("192.168.0.103", "macbook");
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
