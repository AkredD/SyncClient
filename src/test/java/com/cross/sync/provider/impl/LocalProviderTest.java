package com.cross.sync.provider.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.LinuxProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LocalProviderTest {
    private LinuxProvider provider;
    private String testPath;
    private String testDistPath;

    @BeforeEach
    void setUp() {
        provider = new LocalProvider();
        testPath = System.getProperty("user.home") + File.separator + "testFile.temp";
        testDistPath = System.getProperty("user.home") + File.separator + "testDistTest.temp";
    }

    @Test
    @DisplayName("Base actions with file")
    void getMD5FileHash() {
        try {
            provider.createFile(testPath);
            File testFile = new File(testPath);
            assertTrue(testFile.exists());
            assertEquals("d41d8cd98f00b204e9800998ecf8427e", provider.getMD5FileHash(testPath));
            provider.deleteFile(testPath);
            assertFalse(testFile.exists());
        } catch (ProviderException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("Loading file from local")
    void loadFile() {
        try {
            createTestFile();
            File file = new File(testDistPath);
            FileOutputStream out = new FileOutputStream(file);
            out.write(provider.loadFile(testPath).readAllBytes());
            assertEquals("af6c12415e94ed70e45dccd3aa4d2931", provider.getMD5FileHash(testDistPath));
            provider.deleteFile(testPath);
            provider.deleteFile(testDistPath);
            assertFalse(file.exists());
        } catch (IOException | ProviderException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("Uploading file to local")
    void uploadFile() {
        try {
            createTestFile();
            FileInputStream in = new FileInputStream(testPath);
            provider.uploadFile(testDistPath).write(in.readAllBytes());
            assertEquals("af6c12415e94ed70e45dccd3aa4d2931", provider.getMD5FileHash(testDistPath));
            provider.deleteFile(testPath);
            provider.deleteFile(testDistPath);
        } catch (IOException | ProviderException e) {
            e.printStackTrace();
            fail();
        }
    }

    private void createTestFile() throws IOException {
        File testFile = new File(testPath);
        assertTrue(testFile.createNewFile());
        FileOutputStream out = new FileOutputStream(testFile);
        byte[] bytes = {1, 3, 1, 6, 7, 1};
        out.write(bytes);
        out.close();
    }
}