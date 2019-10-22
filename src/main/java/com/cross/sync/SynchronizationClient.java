package com.cross.sync;


import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import net.schmizz.sshj.common.IOUtils;

import java.io.IOException;

public class SynchronizationClient {

    public static void main(String... args) throws ProviderException, IOException {
        String host = "localhost";
        String publocKey = "akredd";
        SSHProvider provider = new SSHProvider(host, publocKey);
        LocalProvider localProvider = new LocalProvider();
        provider.open();
        provider.ping();
        System.out.println("remote md5 " + provider.getMD5FileHash("/home/akredd/temp/test"));
        System.out.println("remote md5 " + provider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println("local md5 " + localProvider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println("local md5 " + localProvider.getMD5FileHash("/home/akredd/temp/empty2"));
        System.out.println(IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toString());
        byte[] content = IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toByteArray();
        provider.uploadFile("/home/akredd/temp/empty2").write(content, 0, content.length);

        provider.close();
    }
}
