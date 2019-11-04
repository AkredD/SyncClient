package com.cross.sync.swing.controller;

import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.transfer.Transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceController {
    private static ResourceController instance;
    private final Map<String, Transfer> transferMap;
    private final Map<String, LinuxProvider> linuxProviderMap;
    private final Map<String, Set<Transfer>> transfersByProvider;

    private ResourceController() {
        this.transferMap = new HashMap<>();
        this.linuxProviderMap = new HashMap<>();
        this.transfersByProvider = new HashMap<>();
    }

    public static ResourceController getInstance() {
        if (instance == null) {
            synchronized (ResourceController.class) {
                if (instance == null) {
                    instance = new ResourceController();
                }
            }
        }
        return instance;
    }

    public Map<String, Transfer> getTransferMap() {
        return transferMap;
    }

    public Map<String, LinuxProvider> getLinuxProviderMap() {
        return linuxProviderMap;
    }

    public Map<String, Set<Transfer>> getTransfersByProvider() {
        return transfersByProvider;
    }
}
