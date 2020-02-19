package com.example.exception;

public class InvalidQueueNameException extends RuntimeException {

    // message took from the real exception
    public InvalidQueueNameException(){
        super ("The name of a FIFO queue can only include alphanumeric characters, hyphens, or underscores, " +
                "must end with .fifo suffix and be 1 to 80 in length.");
    }
}
