package com.cross.sync.provider.impl;

import com.cross.sync.exception.LocalProviderException;
import com.cross.sync.provider.LinuxProvider;

import java.io.*;
import java.util.Random;

public class LocalProvider implements LinuxProvider {

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
            file.createNewFile();
            if (!file.canWrite()) {
                throw new LocalProviderException(String.format("Can't write file: %s", path));
            }
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw new LocalProviderException(e);
        }
    }


    @Override
    public String getMD5FileHash(String path) throws LocalProviderException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "md5sum " + path);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String answer = reader.readLine();
            String result = (answer == null || answer.isBlank()) ? ((Double) new Random().nextDouble()).toString() : answer.split(" ")[0];
            int exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new LocalProviderException(String.format("Can't execute command: {md5sum %s}", path));
            }
            return result;
        } catch (IOException | InterruptedException e) {
            throw new LocalProviderException(e);
        }
    }

    @Override
    public Boolean ping() {
        return true;
    }

    @Override
    public void createFile(String path) throws LocalProviderException {
        try {
            File file = new File(path);
            file.createNewFile();
        } catch (IOException e) {
            throw new LocalProviderException(e);
        }
    }

    @Override
    public void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    @Override
    public void moveFile(String from, String to) throws LocalProviderException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "mv " + from + " " + to);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
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
}
