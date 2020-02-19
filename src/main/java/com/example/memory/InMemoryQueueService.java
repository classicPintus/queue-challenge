package com.example.memory;

import com.example.BaseQueueService;
import com.example.QueueService;
import com.example.dto.MessageToSend;
import com.example.dto.QueueMessage;
import com.example.exception.InvalidQueueNameException;
import com.example.exception.InvalidVisibilityTimeoutException;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class InMemoryQueueService extends BaseQueueService {

    private final Map<QueueWrapper, Deque<QueueMessage>> queues;

    public InMemoryQueueService(QueueNameValidator queueNameValidator,
                                VisibilityTimeoutValidator visibilityTimeoutValidator) {
        super(queueNameValidator, visibilityTimeoutValidator);
        queues = new ConcurrentHashMap<>();
    }

    @Override
    protected void doCreateQueue(String queueName, int visibilityTimeout) {
        this.queues.computeIfAbsent(new QueueWrapper(queueName, visibilityTimeout), key -> new ConcurrentLinkedDeque<>());
    }

    @Override
    public void deleteQueue(String queueName) {
        this.queues.remove(new QueueWrapper(queueName, 0));
    }

    @Override
    public void push(String queueName, MessageToSend messageToSend) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<QueueMessage> pull(String queueName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(String queueName, QueueMessage queueMessage) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
