package com.cross.sync.transfer.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.provider.Provider;
import com.cross.sync.transfer.Transfer;
import com.cross.sync.util.Slf4fLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class FullTempTransfer extends Transfer {
    private final Map<String, Long> lastModifiedHistory;
    private long allSize;
    private long transferedSize;

    public FullTempTransfer(String name, Provider readProvider, String readPath, Provider writeProvider, String writePath) {
        super(readProvider, writeProvider, readPath, writePath);
        this.lastModifiedHistory = new HashMap<>();
        super.log = new StringBuilder();
        setName(name);
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
                String message = String.format("%s: Synchronizing file from %s by path '%s' with %s '%s' was interrupted. Skipped deleting step."
                        , new Date()
                        , readProvider.getClass().getSimpleName(), readPath + readPath
                        , writeProvider.getClass().getSimpleName(), writePath + writePath);
                log.append(message).append("\n");
                Slf4fLogger.info(this, message);
                return;
            }
            var deletedFiles = lastModifiedHistory.keySet()
                    .stream()
                    .filter(cachedFile -> !foundFiles.contains(cachedFile))
                    .peek(deletedFile -> {
                        try {
                            writeProvider.deleteFile(String.format("%s/%s", writePath, deletedFile));
                            var message = String.format("%s: Found deleted file '%s/%s'. Synchronized", new Date(), writePath, deletedFile);
                            log.append(message).append("\n");
                            Slf4fLogger.info(this, message);
                        } catch (ProviderException e) {
                            Slf4fLogger.error(this, e.getMessage());
                        }
                    })
                    .collect(Collectors.toList());
            deletedFiles.forEach(lastModifiedHistory::remove);
            status = 100;
            interrupted = false;
        } catch (ProviderException | IOException e) {
            Slf4fLogger.error(this, e.getMessage());
            interrupted = true;
            log.append(String.format("%s: Something go wrong: %s\n", new Date(), e.getMessage()));
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
                String message = String.format("%s: Synchronizing file from %s by path '%s' with %s '%s' was interrupted"
                        , new Date()
                        , readProvider.getClass().getSimpleName(), readPath + contextReadDirPath
                        , writeProvider.getClass().getSimpleName(), writePath + additionalDirectoryPath);
                log.append(message).append("\n");
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
        if (!lastModifiedHistory.containsKey(additionFilePath)
                || !lastModifiedHistory.get(additionFilePath).equals(readProvider.getMTime(contextReadPath))) {
            String writeTempPath = contextWritePath + ".temp";
            writeProvider.createFile(writeTempPath);
            transferTo(readProvider.loadFile(contextReadPath), writeProvider.uploadFile(writeTempPath));
            if (isInterrupted()) {
                writeProvider.deleteFile(writeTempPath);
                String message = String.format("%s: Synchronizing file from %s by path '%s' with %s '%s' was interrupted"
                        , new Date()
                        , readProvider.getClass().getSimpleName(), contextReadPath
                        , writeProvider.getClass().getSimpleName(), contextWritePath);
                log.append(message).append("\n");
                Slf4fLogger.info(this, message);
                return;
            }
            writeProvider.moveFile(writeTempPath, contextWritePath);
            String message = String.format("%s: File from %s by path '%s' synchronized with %s '%s'"
                    , new Date()
                    , readProvider.getClass().getSimpleName(), contextReadPath
                    , writeProvider.getClass().getSimpleName(), contextWritePath);
            log.append(message).append("\n");
            Slf4fLogger.info(this, message);
            lastModifiedHistory.put(additionFilePath, readProvider.getMTime(contextReadPath));
        } else {
            transferedSize += readProvider.getSize(contextReadPath);
            status = (int) ((transferedSize * 100L) / allSize);
        }
    }

    private void transferTo(InputStream source, OutputStream destination) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = source.read(buffer)) != -1) {
            transferedSize += len;
            status = (int) ((transferedSize * 100L) / allSize);
            destination.write(buffer, 0, len);
            if (interrupted) {
                source.close();
                destination.close();
                return;
            }
        }
        source.close();
        destination.close();
    }

}
