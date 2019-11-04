package com.cross.sync;


import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.provider.impl.SSHProvider;
import com.cross.sync.swing.JSync;
import com.cross.sync.swing.controller.ResourceController;

import java.util.HashSet;

class SynchronizationClient {
    public static void main(String... args) throws ProviderException {
        LinuxProvider provider = new LocalProvider();
        SSHProvider provider1 = new SSHProvider("192.168.0.102", "macbook");
        provider1.open();
        ResourceController.getInstance().getLinuxProviderMap().put("local", provider);
        ResourceController.getInstance().getLinuxProviderMap().put("yarti", provider1);
        ResourceController.getInstance().getTransfersByProvider().put("local", new HashSet<>());
        ResourceController.getInstance().getTransfersByProvider().put("yarti", new HashSet<>());
        com.cross.sync.swing.JSync dialog = new JSync();
        dialog.pack();
        dialog.setVisible(true);
    }
}
