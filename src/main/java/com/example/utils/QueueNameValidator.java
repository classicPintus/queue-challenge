package com.example.utils;

import software.amazon.awssdk.utils.StringUtils;

public class QueueNameValidator {

    public boolean isValid(String queueName){
        return !StringUtils.isBlank(queueName) && queueName.matches("^[a-zA-Z0-9\\-_]{1,75}\\.fifo$");
    }
}
