package com.intor.watchtime.exception;

public class WatchLimitExceededException extends RuntimeException {

    public WatchLimitExceededException(String message) {
        super(message);
    }
}
