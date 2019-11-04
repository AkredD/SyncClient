package com.cross.sync.transfer.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.transfer.Transfer;
import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FullTempTransfer extends Transfer {
    private final LinuxProvider readProvider;
    private final LinuxProvider writeProvider;
    private final String readPath;
    private final String writePath;
    private final Map<String, Long> lastModifiedHistory;
    private long allSize;
    private long transferedSize;

    public FullTempTransfer(LinuxProvider readProvider, String readPath, LinuxProvider writeProvider, String writePath) {
        this.readProvider = readProvider;
        this.writeProvider = writeProvider;
        this.readPath = readPath;
        this.writePath = writePath;
        this.lastModifiedHistory = new HashMap<>();
        super.log = new StringBuilder();
    }

    @Override
    protected synchronized void transferData() {
        try {
            status = 0;
            transferedSize = 0L;
            Set<String> foundFiles = new HashSet<>();
            if (readProvider.isDirectory(readPath)) {
                allSize = getTransferDirSize("/");
                transferDirectory("/", foundFiles);
            } else {
                allSize = getTransferedFileSize("");
                transferFile("", foundFiles);
            }
            if (isInterrupted()) {
                String message = String.format("Synchronizing file from %s by path '%s' with %s '%s' was interrupted. Skipped deleting step."
                        , readProvider.getClass().getSimpleName(), readPath + readPath
                        , writeProvider.getClass().getSimpleName(), writePath + writePath);
                log.append(message);
                Slf4fLogger.info(this, message);
                return;
            }
            var deletedFiles = lastModifiedHistory.keySet()
                    .stream()
                    .filter(cachedFile -> !foundFiles.contains(cachedFile))
                    .peek(deletedFile -> {
                        try {
                            writeProvider.deleteFile(String.format("%s/%s", writePath, deletedFile));
                            var message = String.format("Found deleted file '%s'. Synchronized", String.format("%s/%s", writePath, deletedFile));
                            log.append(message);
                            Slf4fLogger.info(this, message);
                        } catch (ProviderException e) {
                            e.printStackTrace();
                        }
                    })
                    .collect(Collectors.toList());
            deletedFiles.forEach(lastModifiedHistory::remove);
            status = 100;
        } catch (ProviderException | IOException e) {
            Slf4fLogger.error(this, e.getMessage());
        }
    }

    private long getTransferDirSize(String additionalDirectoryPath) throws ProviderException {
        String contextReadDirPath = readPath + additionalDirectoryPath;
        long result = 0L;
        for (String pathToFile : readProvider.getFileList(contextReadDirPath)) {
            if (readProvider.isDirectory(String.format("%s/%s", contextReadDirPath, pathToFile))) {
                result += getTransferDirSize(String.format("%s/%s", additionalDirectoryPath, pathToFile));
            } else {
                result += getTransferedFileSize(String.format("%s/%s", additionalDirectoryPath, pathToFile));
            }
        }
        return result;
    }

    private long getTransferedFileSize(String additionalFilePath) throws ProviderException {
        String contextReadFilePath = readPath + additionalFilePath;
        return readProvider.getSize(contextReadFilePath);
    }


    private void transferDirectory(String additionalDirectoryPath, Set<String> foundFiles) throws ProviderException, IOException {
        String contextReadDirPath = readPath + additionalDirectoryPath;
        String contextWriteDirPath = writePath + additionalDirectoryPath;
        for (String pathToFile : readProvider.getFileList(contextReadDirPath)) {
            if (readProvider.isDirectory(String.format("%s/%s", contextReadDirPath, pathToFile))) {
                if (!writeProvider.existFile(String.format("%s/%s", contextWriteDirPath, pathToFile))
                        || !writeProvider.isDirectory(String.format("%s/%s", contextWriteDirPath, pathToFile))) {
                    writeProvider.createDirectory(String.format("%s/%s", contextWriteDirPath, pathToFile));
                }
                transferDirectory(String.format("%s/%s", additionalDirectoryPath, pathToFile), foundFiles);
            } else {
                transferFile(String.format("%s/%s", additionalDirectoryPath, pathToFile), foundFiles);
            }
            if (isInterrupted()) {
                String message = String.format("Synchronizing file from %s by path '%s' with %s '%s' was interrupted"
                        , readProvider.getClass().getSimpleName(), readPath + contextReadDirPath
                        , writeProvider.getClass().getSimpleName(), writePath + additionalDirectoryPath);
                log.append(message);
                Slf4fLogger.info(this, message);
                return;
            }
        }
        foundFiles.add(additionalDirectoryPath);
        lastModifiedHistory.put(additionalDirectoryPath, -1L);
    }

    private void transferFile(String additionFilePath, Set<String> foundFiles) throws ProviderException, IOException {
        String contextReadPath = readPath + additionFilePath;
        String contextWritePath = writePath + additionFilePath;
        foundFiles.add(additionFilePath);
        transferedSize += readProvider.getSize(contextReadPath);
        status = (int) ((transferedSize * 100L) / allSize);
        if (!lastModifiedHistory.containsKey(additionFilePath)
                || !lastModifiedHistory.get(additionFilePath).equals(readProvider.getMTime(contextReadPath))) {
            String writeTempPath = contextWritePath + ".temp";
            writeProvider.createFile(writeTempPath);
            source = readProvider.loadFile(contextReadPath);
            destination = writeProvider.uploadFile(writeTempPath);
            super.transferTo();
            if (isInterrupted()) {
                writeProvider.deleteFile(writeTempPath);
                String message = String.format("Synchronizing file from %s by path '%s' with %s '%s' was interrupted"
                        , readProvider.getClass().getSimpleName(), contextReadPath
                        , writeProvider.getClass().getSimpleName(), contextWritePath);
                log.append(message);
                Slf4fLogger.info(this, message);
                return;
            }
            source.close();
            destination.close();
            writeProvider.moveFile(writeTempPath, contextWritePath);
            String message = String.format("File from %s by path '%s' synchronized with %s '%s'"
                    , readProvider.getClass().getSimpleName(), contextReadPath
                    , writeProvider.getClass().getSimpleName(), contextWritePath);
            log.append(message);
            Slf4fLogger.info(this, message);
            lastModifiedHistory.put(additionFilePath, readProvider.getMTime(contextReadPath));
        }
    }

}
