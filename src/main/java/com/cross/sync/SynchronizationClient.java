package com.cross.sync;


import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.swing.JSync;
import com.cross.sync.swing.controller.ResourceController;

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

    public static void main(String... args) {
        LinuxProvider provider = new LocalProvider();
        ResourceController.getInstance().getLinuxProviderMap().put("local", provider);
        ResourceController.getInstance().getTransfersByProvider().put("local", new HashSet<>());
        com.cross.sync.swing.JSync dialog = new JSync();
        dialog.pack();
        dialog.setVisible(true);
    }
}
