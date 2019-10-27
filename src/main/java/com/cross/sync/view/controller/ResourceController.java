package com.cross.sync.view.controller;

import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.transfer.Transfer;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceController {
    private static ResourceController instance;
    private Map<String, Transfer> transferMap;
    private Map<String, LinuxProvider> linuxProviderMap;

    private ResourceController() {
        this.transferMap = new LinkedHashMap<>();
        this.linuxProviderMap = new LinkedHashMap<>();
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
}
