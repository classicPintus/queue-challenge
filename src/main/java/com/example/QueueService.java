package com.example;

import com.example.exception.InvalidQueueNameException;
import com.example.exception.InvalidVisibilityTimeoutException;
import com.example.dto.QueueMessage;
import com.example.dto.MessageToSend;

import java.util.Optional;

public interface QueueService {

    /**
     * Creates a new queue.
     *
     * @param queueName queue name
     * @param visibilityTimeout sqs visibility timeout in seconds
     * @throws InvalidQueueNameException when the name is not null and doesn't respects all the aws constraints
     * @throws InvalidVisibilityTimeoutException when the visibility timeout doesn't respects all the aws constraints
     */
    void createQueue(String queueName, int visibilityTimeout);

    /**
     * Deletes a queue
     *
     * @param queueName queue name to remove
     */
    void deleteQueue(String queueName);

    /**
     * Pushes a new message into the specified queue
     *
     * @param queueName target queue name
     * @param messageToSend message to push
     */
    void push(String queueName, MessageToSend messageToSend);

    /**
     * Pulls a message from the queue
     *
     * @param queueName target queue name
     * @return the message on the head or Optional.empty()
     */
    Optional<QueueMessage> pull(String queueName);

    /**
     * Deletes a message previously deleted
     *
     * @param queueName target queue name
     * @param queueMessage message to delete
     */
    void delete(String queueName, QueueMessage queueMessage);

}
