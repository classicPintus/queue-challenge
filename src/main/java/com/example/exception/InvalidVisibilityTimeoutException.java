package com.example.exception;

import com.example.utils.QueueChallengeUtils;

public class InvalidVisibilityTimeoutException extends RuntimeException {

    public InvalidVisibilityTimeoutException() {
        super("Visibility timeout must be in the interval [" +
                QueueChallengeUtils.MIN_VISIBILITY_TIMEOUT + "," + QueueChallengeUtils.MAX_VISIBILITY_TIMEOUT + "]");
    }
}
