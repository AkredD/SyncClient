package com.cross.sync.provider.impl;

import com.cross.sync.exception.ProviderException;
import com.cross.sync.exception.SSHProviderException;
import com.cross.sync.provider.LinuxProvider;
import com.cross.sync.util.RemoteInputStream;
import com.cross.sync.util.RemoteOutputStream;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SSHProvider implements Closeable, LinuxProvider {
    private final static Set<OpenMode> READ_MODE;
    private final static Set<OpenMode> WRITE_MODE;

    static {
        READ_MODE = new HashSet<>();
        WRITE_MODE = new HashSet<>();
        READ_MODE.add(OpenMode.READ);
        WRITE_MODE.add(OpenMode.WRITE);
    }

    private String host;
    private String publicKey;
    private SSHClient ssh;

    public SSHProvider(String host, String publicKey) {
        this.host = host;
        this.publicKey = publicKey;
    }

    @Override
    public InputStream loadFile(String path) throws SSHProviderException {
        RemoteFile rf;
        try {
            rf = ssh.newSFTPClient().open(path, READ_MODE);
            return new RemoteInputStream(rf);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public OutputStream uploadFile(String path) throws SSHProviderException {
        RemoteFile rf;
        try {
            createFile(path);
            SFTPClient sftpClient = ssh.newSFTPClient();
            sftpClient.getFileTransfer().setPreserveAttributes(false);
            rf = sftpClient.open(path, WRITE_MODE);
            return new RemoteOutputStream(rf);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public void createFile(String path) throws SSHProviderException {
        try {
            try (Session session = ssh.startSession()) {
                final Session.Command cmd = session.exec("touch " + path);
            }
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }

    }

    @Override
    public void deleteFile(String path) throws SSHProviderException {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec("rm " + path);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }

    }

    @Override
    public void moveFile(String from, String to) throws ProviderException {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec("mv " + from + " " + to);
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
    }

    @Override
    public String getMD5FileHash(String path) throws SSHProviderException {
        String result;
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec("md5sum " + path);
            String answer = IOUtils.readFully(cmd.getInputStream()).toString();
            result = (answer == null || answer.isBlank()) ? ((Double) new Random().nextDouble()).toString() : answer.split(" ")[0];
        } catch (IOException e) {
            throw new SSHProviderException(e);
        }
        return result;
    }

    @Override
    public Boolean ping() {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec("ping -c 1 google.com");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void open() throws ProviderException {
        try {
            this.ssh = new SSHClient();
            this.ssh.addHostKeyVerifier(new PromiscuousVerifier());
            this.ssh.loadKnownHosts();
            this.ssh.connect(host);
            this.ssh.authPublickey(publicKey);
        } catch (IOException e) {
            throw new SSHProviderException(String.format("Can't connect via ssh to %s with login %s", host, publicKey), e);
        }
    }

    @Override
    public void close() throws IOException {
        ssh.disconnect();
    }

    @Override
    public Boolean existFile(String path) {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec(String.format("test -f %s && echo \"Yes\" || echo \"No\"", path));
            String answer = IOUtils.readFully(cmd.getInputStream()).toString();
            return answer.startsWith("Yes");
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean canRead(String path) {
        try (Session session = ssh.startSession()) {
            final Session.Command cmd = session.exec(String.format("test -r %s && echo \"Yes\" || echo \"No\"", path));
            String answer = IOUtils.readFully(cmd.getInputStream()).toString();
            return answer.startsWith("Yes");
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean canWrite(String path) {
        try (Session session = ssh.startSession()) {
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
            final Session.Command cmd = session.exec(String.format("test -w %s && echo \"Yes\" || echo \"No\"", writableCheckingPath));
            String answer = IOUtils.readFully(cmd.getInputStream()).toString();
            return answer.startsWith("Yes");
        } catch (IOException e) {
            return false;
        }
    }
}
