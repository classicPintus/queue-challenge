package com.example;

import com.example.exception.InvalidQueueNameException;
import com.example.message.MessageReceived;
import com.example.message.MessageToSend;
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
    public void createQueue(String queueName) {

        if (StringUtils.isBlank(queueName) || !queueName.matches("^[a-zA-Z0-9\\-_]{1,75}\\.fifo$")) {
            throw new InvalidQueueNameException();
        }

        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.FIFO_QUEUE, "true");
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
    public Optional<MessageReceived> pull(String queueName) {
        Optional<MessageReceived> res = Optional.empty();

        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(getQueueUrl(queueName))
                .maxNumberOfMessages(1)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

        if (!CollectionUtils.isNullOrEmpty(messages)) {
            Message messageFromAws = messages.get(0);
            res = Optional.of(new MessageReceived(messageFromAws.body(), messageFromAws.receiptHandle()));
        }

        return res;
    }

    @Override
    public void delete(String queueName, MessageReceived messageReceived) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(getQueueUrl(queueName))
                .receiptHandle(messageReceived.getUniqueIdentifier())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    private String getQueueUrl(String queueName) {
        GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();
        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(request);
        return getQueueUrlResponse.queueUrl();
    }
}
