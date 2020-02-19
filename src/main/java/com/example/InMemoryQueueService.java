package com.example;

import com.example.message.MessageReceived;
import com.example.message.MessageToSend;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class InMemoryQueueService implements QueueService {

    private Map<String, Deque<MessageReceived>> queues;

    public InMemoryQueueService() {
        queues = new ConcurrentHashMap<>();
    }

    @Override
    public void createQueue(String queueName) {
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
    public Optional<MessageReceived> pull(String queueName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(String queueName, MessageReceived messageReceived) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
