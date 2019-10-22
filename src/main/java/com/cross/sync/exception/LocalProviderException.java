package com.cross.sync.exception;

public class LocalProviderException extends ProviderException {
    public LocalProviderException(String message) {
        super(message);
    }

    public LocalProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalProviderException(Throwable cause) {
        super(cause);
    }
}
