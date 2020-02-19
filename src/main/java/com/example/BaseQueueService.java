package com.example;

import com.example.exception.InvalidQueueNameException;
import com.example.exception.InvalidVisibilityTimeoutException;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;

public abstract class BaseQueueService implements QueueService {

    private final QueueNameValidator queueNameValidator;
    private final VisibilityTimeoutValidator visibilityTimeoutValidator;

    public BaseQueueService(QueueNameValidator queueNameValidator,
                            VisibilityTimeoutValidator visibilityTimeoutValidator) {
        this.queueNameValidator = queueNameValidator;
        this.visibilityTimeoutValidator = visibilityTimeoutValidator;
    }

    @Override
    public void createQueue(String queueName, int visibilityTimeout) {
        if (!queueNameValidator.isValid(queueName)) {
            throw new InvalidQueueNameException();
        }

        if (!visibilityTimeoutValidator.isValid(visibilityTimeout)) {
            throw new InvalidVisibilityTimeoutException();
        }

        doCreateQueue(queueName, visibilityTimeout);
    }

    protected abstract void doCreateQueue(String queueName, int visibilityTimeout);
}
