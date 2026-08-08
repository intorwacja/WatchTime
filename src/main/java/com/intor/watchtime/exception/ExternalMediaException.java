package com.intor.watchtime.exception;

public class ExternalMediaException extends RuntimeException {
    public ExternalMediaException(String message) {
        super(message);
    }

    public ExternalMediaException(Long id) {
        super("External media with id " + id + " not found");
    }

    public ExternalMediaException(String message, Throwable cause) {
        super(message, cause);
    }
}
