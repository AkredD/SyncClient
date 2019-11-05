package com.cross.sync.provider.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.exception.SSHProviderException;
import com.cross.sync.provider.Provider;
import com.cross.sync.util.RemoteInputStream;
import com.cross.sync.util.RemoteOutputStream;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.*;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FilePermission;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SSHProvider implements Provider {
    private final static Set<OpenMode> READ_MODE;
    private final static Set<OpenMode> WRITE_MODE;
    private final static Set<OpenMode> CREATE_MODE;

    static {
        READ_MODE = new HashSet<>();
        WRITE_MODE = new HashSet<>();
        CREATE_MODE = new HashSet<>();
        READ_MODE.add(OpenMode.READ);
        WRITE_MODE.add(OpenMode.WRITE);
        CREATE_MODE.add(OpenMode.CREAT);
        CREATE_MODE.add(OpenMode.WRITE);
    }

    private String host;
    private String publicKey;
    private SSHClient ssh;
    private SFTPClient sftpClient;

    public SSHProvider(String host, String publicKey) {
        this.host = host;
        this.publicKey = publicKey;
    }

    @Override
    public InputStream loadFile(String path) throws SSHProviderException {
        RemoteFile rf;
        try {
            rf = sftpClient.open(path, READ_MODE);
            return new RemoteInputStream(rf);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public OutputStream uploadFile(String path) throws SSHProviderException {
        RemoteFile rf;
        try {
            sftpClient.getFileTransfer().setPreserveAttributes(false);
            rf = sftpClient.open(path, WRITE_MODE);
            return new RemoteOutputStream(rf);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public Long getMTime(String path) throws ProviderException {
        try {
            return sftpClient.lstat(path).getMtime();
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public void createFile(String path) throws SSHProviderException {
        try {
            sftpClient.open(path, CREATE_MODE).close();
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }

    }

    @Override
    public void createDirectory(String path) throws ProviderException {
        try {
            sftpClient.mkdir(path);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public void deleteFile(String path) throws SSHProviderException {
        try {
            sftpClient.rm(path);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }

    }

    // TODO mv ???
    @SuppressWarnings("unused")
    @Override
    public void moveFile(String from, String to) throws ProviderException {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec(String.format("mv \"%s\" \"%s\"", from, to));
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public Boolean ping() {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec("ping -c 1 google.com");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void open() throws ProviderException {
        try {
            this.ssh = new SSHClient();
            this.ssh.addHostKeyVerifier(new PromiscuousVerifier());
            this.ssh.loadKnownHosts();
            this.ssh.connect(host);
            this.ssh.authPublickey(publicKey);
            this.sftpClient = ssh.newSFTPClient();
        } catch (IOException e) {
            throw new SSHProviderException(String.format("Can't connect via ssh to %s with login %s", host, publicKey), e);
        }
    }

    @Override
    public void close() throws IOException {
        sftpClient.close();
        ssh.disconnect();
    }

    @Override
    public Boolean existFile(String path) {
        try {
            FileMode.Type fileType = sftpClient.lstat(path).getType();
            return fileType.equals(FileMode.Type.DIRECTORY) || fileType.equals(FileMode.Type.REGULAR);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean canRead(String path) {
        try {
            return sftpClient.lstat(path).getPermissions().contains(FilePermission.USR_R);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean canWrite(String path) {
        try {
            return sftpClient.lstat(path).getPermissions().contains(FilePermission.USR_W);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean isDirectory(String path) throws ProviderException {
        try {
            return sftpClient.lstat(path).getType().equals(FileMode.Type.DIRECTORY);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public List<String> getFileList(String path) throws ProviderException {
        try {
            return sftpClient.ls(path)
                    .stream()
                    .map(RemoteResourceInfo::getName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public Long getSize(String path) throws ProviderException {
        try {
            return sftpClient.lstat(path).getSize();
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }
}
