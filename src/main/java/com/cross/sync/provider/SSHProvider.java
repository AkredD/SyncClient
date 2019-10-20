package com.cross.sync.provider;

import com.cross.sync.util.RemoteInputStream;
import com.cross.sync.util.RemoteOutputStream;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class SSHProvider implements Closeable {
    private String host;
    private String publicKey;
    private SSHClient ssh;
    private final static Set<OpenMode> READ_MODE;
    private final static Set<OpenMode> WRITE_MODE;
    static {
        READ_MODE = new HashSet<>();
        WRITE_MODE = new HashSet<>();
        READ_MODE.add(OpenMode.READ);
        WRITE_MODE.add(OpenMode.WRITE);
    }

    public SSHProvider(String host, String publicKey) {
        this.host = host;
        this.publicKey = publicKey;
    }

    public InputStream loadFile(String path) throws IOException {
        RemoteFile rf = ssh.newSFTPClient().open(path, READ_MODE);
        return new RemoteInputStream(rf);
    }

    public OutputStream uploadFile(String path) throws IOException {
        createFile(path);
        RemoteFile rf = ssh.newSFTPClient().open(path, WRITE_MODE);
        return new RemoteOutputStream(rf);
    }

    public void createFile(String path) throws IOException {
        Session session = null;
        try {
            session = ssh.startSession();
            final Session.Command cmd = session.exec("echo '' > " + path);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                // Do Nothing
            }
        }
    }

    public void ping() throws IOException {
        Session session = null;
        try {
            session = ssh.startSession();
            final Session.Command cmd = session.exec("ping -c 1 google.com");
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                // Do Nothing
            }
        }
    }

    public void open() throws IOException {
        this.ssh = new SSHClient();
        this.ssh.addHostKeyVerifier(new PromiscuousVerifier());
        this.ssh.loadKnownHosts();
        this.ssh.connect(host);
        this.ssh.authPublickey(publicKey);
    }

    @Override
    public void close() throws IOException {
        ssh.disconnect();
    }
}
