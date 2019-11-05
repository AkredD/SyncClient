package com.cross.sync;


import com.cross.sync.provider.Provider;
import com.cross.sync.provider.impl.LocalProvider;
import com.cross.sync.swing.JSync;
import com.cross.sync.swing.controller.ResourceController;
import com.cross.sync.transfer.impl.FullTempTransfer;

import java.util.Arrays;
import java.util.HashSet;

class SynchronizationClient {
    public static void main(String... args) {
        Provider provider = new LocalProvider("local");
        //SSHProvider provider1 = new SSHProvider("192.168.0.102", "macbook");
        //provider1.open();
        ResourceController.getInstance().getLinuxProviderMap().put("local", provider);
        //ResourceController.getInstance().getLinuxProviderMap().put("yarti", provider1);
        ResourceController.getInstance().getTransfersByProvider().put("local", new HashSet<>());
        //ResourceController.getInstance().getTransfersByProvider().put("yarti", new HashSet<>());
        ResourceController.getInstance().getTransferMap().put("test", new FullTempTransfer("test", provider, "/home/akredd/test", provider, "/home/akredd/dest"));
        ResourceController.getInstance().getTransferMap().put("tdefst", new FullTempTransfer("tdefst", provider, "/home/akredd/test", provider, "/home/akredd/dest"));
        ResourceController.getInstance().getTransfersByProvider().put("local", new HashSet<>(Arrays.asList(ResourceController.getInstance().getTransferMap().get("test")
                , ResourceController.getInstance().getTransferMap().get("tdefst"))));
        com.cross.sync.swing.JSync dialog = new JSync();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
