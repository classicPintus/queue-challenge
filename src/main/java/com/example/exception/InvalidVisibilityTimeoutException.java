package com.example.exception;

import com.example.utils.QueueChallengeUtils;

public class InvalidVisibilityTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -1480542138979998614L;

    public InvalidVisibilityTimeoutException() {
        super("Visibility timeout must be in the interval [" +
                QueueChallengeUtils.MIN_VISIBILITY_TIMEOUT + "," + QueueChallengeUtils.MAX_VISIBILITY_TIMEOUT + "]");
    }
}
