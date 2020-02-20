package com.example.exception;

public class UnknownQueueException extends RuntimeException {

    private static final long serialVersionUID = 4035579367361320888L;

    public UnknownQueueException() {}

    public UnknownQueueException(Throwable cause) {
        super(cause);
    }
}
