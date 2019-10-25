package com.cross.sync;


import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;

import java.io.IOException;

public class SynchronizationClient {

    public static void main(String... args) throws ProviderException, IOException, InterruptedException {
        String host = "localhost";
        String publicKey = "akredd";
        SSHProvider provider = new SSHProvider(host, publicKey);
        provider.open();
        LocalProvider localProvider = new LocalProvider();
        provider.ping();
        provider.uploadFile("~/tempTemp.temp");
        /*System.out.println("remote md5 " + provider.getMD5FileHash("/home/akredd/temp/test"));
        System.out.println("remote md5 " + provider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println("local md5 " + localProvider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println("local md5 " + localProvider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println(IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toString());
        byte[] content = IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toByteArray();
        provider.uploadFile("/home/akredd/temp/empty2").write(content, 0, content.length);*/
        //FullTempTransfer tempTransfer = new FullTempTransfer(provider, "/home/akredd/temp/test", localProvider, "/home/akredd/temp/testTest");
        //tempTransfer.start();
        //tempTransfer.join();
        provider.close();
    }
}
