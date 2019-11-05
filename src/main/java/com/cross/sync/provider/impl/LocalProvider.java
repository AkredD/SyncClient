package com.cross.sync.provider.impl;

import com.cross.sync.exception.LocalProviderException;
import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.Provider;
import com.cross.sync.util.Slf4fLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalProvider implements Provider {

    @Override
    public InputStream loadFile(String path) throws LocalProviderException {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new LocalProviderException(String.format("File doesn't exists: %s", path));
            }
            if (!file.canRead()) {
                throw new LocalProviderException(String.format("Can't read file: %s", path));
            }
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new LocalProviderException(e);
        }
    }

    @Override
    public OutputStream uploadFile(String path) throws LocalProviderException {
        try {
            File file = new File(path);
            if (!file.canWrite()) {
                throw new LocalProviderException(String.format("Can't write file: %s", path));
            }
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw new LocalProviderException(e);
        }
    }

    @Override
    public Long getMTime(String path) {
        File file = new File(path);
        return file.lastModified();
    }

    @Override
    public Boolean ping() {
        return true;
    }

    @Override
    public void createFile(String path) throws LocalProviderException {
        try {
            File file = new File(path);
            if (!file.createNewFile()) {
                Slf4fLogger.info(this, String.format("Can't create file '%s'", path));
            }
        } catch (IOException e) {
            throw new LocalProviderException(e);
        }
    }

    @Override
    public void createDirectory(String path) {
        File file = new File(path);
        if (!file.mkdir()) {
            Slf4fLogger.info(this, String.format("Can't create directory '%s'", path));
        }
    }

    @Override
    public void deleteFile(String path) {
        File file = new File(path);
        if (!file.delete()) {
            Slf4fLogger.info(this, String.format("File '%s' already deleted", path));
        }
    }

    @Override
    public void moveFile(String from, String to) throws LocalProviderException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "mv " + from + " " + to);
        try {
            Process process = processBuilder.start();
            int exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new LocalProviderException(String.format("Can't execute command: {mv %s %s}", from, to));
            }
        } catch (IOException | InterruptedException e) {
            throw new LocalProviderException(e);
        }
    }

    @Override
    public Boolean existFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    @Override
    public Boolean canRead(String path) {
        File file = new File(path);
        return file.canRead();
    }

    @Override
    public Boolean canWrite(String path) {
        String writableCheckingPath = path;
        if (!existFile(path)) {
            String[] separatedPartsOfPath = path.split("/");
            StringBuilder pathBuilder = new StringBuilder("/");
            for (int i = 0; i < separatedPartsOfPath.length - 1; ++i) {
                pathBuilder.append(separatedPartsOfPath[i]);
                pathBuilder.append("/");
            }
            writableCheckingPath = pathBuilder.toString();
        }
        File file = new File(writableCheckingPath);
        return file.canWrite();
    }

    @Override
    public Boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    @Override
    public List<String> getFileList(String path) throws ProviderException {
        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new LocalProviderException(String.format("'%s' is not directory", path));
        }
        if (directory.listFiles() == null) {
            return new ArrayList<>();
        }
        return Stream.of(Objects.requireNonNull(directory.listFiles()))
                .map(File::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Long getSize(String path) {
        File file = new File(path);
        return file.length();
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }
}
