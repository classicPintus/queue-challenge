package com.example.exception;

public class UnknownQueueException extends RuntimeException {

    public UnknownQueueException() {}

    public UnknownQueueException(Throwable cause) {
        super(cause);
    }
}
