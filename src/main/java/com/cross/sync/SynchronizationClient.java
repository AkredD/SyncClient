package com.cross.sync;


import com.cross.sync.provider.SSHProvider;
import net.schmizz.sshj.common.IOUtils;

import java.io.IOException;

public class SynchronizationClient {

    public static void main(String... args) throws IOException {
        String host = "localhost";
        String publocKey = "akredd";
        SSHProvider provider = new SSHProvider(host, publocKey);
        provider.open();
        provider.ping();
        System.out.println(IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toString());
        byte[] content = IOUtils.readFully(provider.loadFile("/home/akredd/temp/test")).toByteArray();
        provider.uploadFile("/home/akredd/temp/empty2").write(content, 0, content.length);
        provider.close();
    }
}
