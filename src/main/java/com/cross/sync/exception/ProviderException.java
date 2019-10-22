package com.cross.sync.exception;

public class ProviderException extends Exception {
    ProviderException(String message) {
        super(message);
    }

    ProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    ProviderException(Throwable cause) {
        super(cause);
    }
}
