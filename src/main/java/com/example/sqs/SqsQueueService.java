package com.example.sqs;

import com.example.QueueChallengeUtils;
import com.example.QueueService;
import com.example.exception.InvalidQueueNameException;
import com.example.exception.InvalidVisibilityTimeoutException;
import com.example.dto.QueueMessage;
import com.example.dto.MessageToSend;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;

import java.util.*;

public class SqsQueueService implements QueueService {
    private final SqsClient sqsClient;

    public SqsQueueService() {
        sqsClient = SqsClient.builder()
                .build();
    }

    @Override
    public void createQueue(String queueName, int visibilityTimeout) {

        if (StringUtils.isBlank(queueName) || !queueName.matches("^[a-zA-Z0-9\\-_]{1,75}\\.fifo$")) {
            throw new InvalidQueueNameException();
        }

        if (visibilityTimeout < QueueChallengeUtils.MIN_VISIBILITY_TIMEOUT ||
                visibilityTimeout > QueueChallengeUtils.MAX_VISIBILITY_TIMEOUT) {
            throw new InvalidVisibilityTimeoutException();
        }

        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.FIFO_QUEUE, "true");
        attributes.put(QueueAttributeName.VISIBILITY_TIMEOUT, String.valueOf(visibilityTimeout));
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .attributes(attributes)
                .build();
        sqsClient.createQueue(createQueueRequest);
    }

    @Override
    public void deleteQueue(String queueName) {
        DeleteQueueRequest request = DeleteQueueRequest.builder().queueUrl(getQueueUrl(queueName)).build();
        sqsClient.deleteQueue(request);
    }

    @Override
    public void push(String queueName, MessageToSend messageToSend) {
        String queueUrl = getQueueUrl(queueName);

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageGroupId(queueName)
                .messageDeduplicationId(UUID.randomUUID().toString())
                .messageBody(messageToSend.getBody())
                .build());
    }

    @Override
    public Optional<QueueMessage> pull(String queueName) {
        Optional<QueueMessage> res = Optional.empty();

        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(getQueueUrl(queueName))
                .maxNumberOfMessages(1)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

        if (!CollectionUtils.isNullOrEmpty(messages)) {
            Message messageFromAws = messages.get(0);
            res = Optional.of(new QueueMessage(messageFromAws.body(), messageFromAws.receiptHandle()));
        }

        return res;
    }

    @Override
    public void delete(String queueName, QueueMessage queueMessage) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(getQueueUrl(queueName))
                .receiptHandle(queueMessage.getUniqueIdentifier())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    private String getQueueUrl(String queueName) {
        GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();
        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(request);
        return getQueueUrlResponse.queueUrl();
    }
}
