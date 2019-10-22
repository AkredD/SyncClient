package com.cross.sync.exception;

public class SSHProviderException extends ProviderException {
    public SSHProviderException(String message) {
        super(message);
    }

    public SSHProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSHProviderException(Throwable cause) {
        super(cause);
    }
}
