package com.example.memory;

import com.example.BaseQueueService;
import com.example.dto.MessageToSend;
import com.example.dto.QueueMessage;
import com.example.exception.UnknownQueueException;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryQueueService extends BaseQueueService {

    private static final Timer TIMER = new Timer("In Memory Visibility Timeout Timer", true);
    private final Map<QueueWrapper, Deque<QueueMessage>> queues;
    private final Map<QueueWrapper, List<QueueMessage>> hiddenMessages;

    public InMemoryQueueService(QueueNameValidator queueNameValidator,
                                VisibilityTimeoutValidator visibilityTimeoutValidator) {
        super(queueNameValidator, visibilityTimeoutValidator);
        queues = new ConcurrentHashMap<>();
        hiddenMessages = new ConcurrentHashMap<>();
    }

    @Override
    protected void doCreateQueue(String queueName, int visibilityTimeout) {
        QueueWrapper queue = new QueueWrapper(queueName, visibilityTimeout);
        this.queues.computeIfAbsent(queue, key -> new ConcurrentLinkedDeque<>());
        this.hiddenMessages.computeIfAbsent(queue, key -> new CopyOnWriteArrayList<>());
    }

    @Override
    public void deleteQueue(String queueName) {
        QueueWrapper queue = getQueue(queueName);
        this.queues.remove(queue);
        this.hiddenMessages.remove(queue);
    }

    @Override
    public void push(String queueName, MessageToSend messageToSend) {
        checkQueueExistence(queueName);

        QueueWrapper queue = getQueue(queueName);
        QueueMessage queueMessage = new QueueMessage(messageToSend.getBody(), UUID.randomUUID().toString());
        queues.get(queue).offer(queueMessage);
    }

    @Override
    public Optional<QueueMessage> pull(String queueName) {
        Optional<QueueMessage> res;
        checkQueueExistence(queueName);

        QueueWrapper queue = getQueue(queueName);
        QueueMessage queueMessage = queues.get(queue).poll();
        if (queueMessage != null) {
            res = Optional.of(queueMessage);
            hiddenMessages.get(queue).add(queueMessage);
            TIMER.schedule(new VisibilityTimeoutTimerTask(queueMessage, queue), queue.getVisibilityTimeout() * 1000L);
        } else {
            res = Optional.empty();
        }

        return res;
    }

    @Override
    public void delete(String queueName, QueueMessage queueMessage) {
        checkQueueExistence(queueName);
        hiddenMessages.get(getQueue(queueName)).remove(queueMessage);
    }

    private QueueWrapper getQueue(String queueName) {
        return queues.keySet().stream()
                .filter(k -> k.equals(new QueueWrapper(queueName)))
                .findFirst()
                .orElseThrow(UnknownQueueException::new);
    }

    private void checkQueueExistence(String queueName) {
        if (!queues.containsKey(new QueueWrapper(queueName, 0))) {
            throw new UnknownQueueException();
        }
    }

    private class VisibilityTimeoutTimerTask extends TimerTask {

        private final QueueMessage queueMessage;
        private final QueueWrapper queue;

        private VisibilityTimeoutTimerTask(QueueMessage queueMessage, QueueWrapper queue) {
            this.queueMessage = queueMessage;
            this.queue = queue;
        }

        @Override
        public void run() {
            if (hiddenMessages.containsKey(queue) &&
                    hiddenMessages.get(queue).contains(queueMessage)) {
                queues.get(queue).addFirst(queueMessage);
                hiddenMessages.get(queue).remove(queueMessage);
            }
        }
    }
}
