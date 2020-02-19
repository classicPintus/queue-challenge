package com.example.memory;

import com.example.QueueService;
import com.example.dto.QueueMessage;
import com.example.dto.MessageToSend;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class InMemoryQueueService implements QueueService {

    private Map<String, Deque<QueueMessage>> queues;

    public InMemoryQueueService() {
        queues = new ConcurrentHashMap<>();
    }

    @Override
    public void createQueue(String queueName, int visibilityTimeout) {
        this.queues.computeIfAbsent(queueName, key -> new ConcurrentLinkedDeque<>());
    }

    @Override
    public void deleteQueue(String queueName) {
        this.queues.remove(queueName);
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
