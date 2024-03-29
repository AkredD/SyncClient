package com.cross.sync.provider.impl;

import com.cross.sync.exception.ProviderException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SSHProviderTest {
    private SSHProvider provider;
    private String testPath;

    @BeforeEach
    void setUp() {
        provider = new SSHProvider("", "localhost", System.getProperty("user.name"));
        try {
            provider.open();
            provider.ping();
        } catch (ProviderException e) {
            e.printStackTrace();
            fail();
        }
        testPath = System.getProperty("user.home") + "/testFile.temp";
    }


    @AfterEach
    void tearDown() {
        provider.close();
    }

    @Test
    @DisplayName("Uploading file from remote")
    void uploadFile() {
        try {
            byte[] bytes = {1, 1, 1, 2, 3, 4, 5, 10, 125, 12};
            provider.uploadFile(testPath).write(bytes);
            provider.deleteFile(testPath);
            assertFalse(provider.existFile(testPath));
        } catch (IOException | ProviderException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("Loading file from remote")
    void loadFile() {
        try {
            byte[] bytes = {1, 1, 1, 2, 3, 4, 5, 10, 125, 12};
            provider.uploadFile(testPath).write(bytes);
            byte[] loadedBytes = provider.loadFile(testPath).readAllBytes();
            assertEquals(bytes.length, loadedBytes.length);
            for (int i = 0; i < bytes.length; ++i) {
                assertEquals(bytes[i], loadedBytes[i]);
            }
            provider.deleteFile(testPath);
            assertFalse(provider.existFile(testPath));
        } catch (IOException | ProviderException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("Base actions with remote file")
    void getMD5FileHash() {
        try {
            byte[] bytes = {1, 1, 1, 2, 3, 4, 5, 10, 125, 12};
            provider.uploadFile(testPath).write(bytes);
            provider.deleteFile(testPath);
            assertFalse(provider.existFile(testPath));
        } catch (IOException | ProviderException e) {
            e.printStackTrace();
            fail();
        }
    }
}