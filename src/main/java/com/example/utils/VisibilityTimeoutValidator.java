package com.example.utils;

public class VisibilityTimeoutValidator {

    public boolean isValid(int visibilityTimeout){
        return visibilityTimeout >= QueueChallengeUtils.MIN_VISIBILITY_TIMEOUT &&
                visibilityTimeout <= QueueChallengeUtils.MAX_VISIBILITY_TIMEOUT;
    }
}
