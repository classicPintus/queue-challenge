package com.example.sqs;

import com.example.BaseQueueService;
import com.example.dto.MessageToSend;
import com.example.dto.QueueMessage;
import com.example.exception.UnknownQueueException;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.*;

public class SqsQueueService extends BaseQueueService {
    private final SqsClient sqsClient;

    public SqsQueueService(QueueNameValidator queueNameValidator,
                           VisibilityTimeoutValidator visibilityTimeoutValidator) {
        super(queueNameValidator, visibilityTimeoutValidator);
        this.sqsClient = SqsClient.builder().build();
    }

    @Override
    protected void doCreateQueue(String queueName, int visibilityTimeout) {
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
        try{
            GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();
            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(request);
            return getQueueUrlResponse.queueUrl();
        }catch(QueueDoesNotExistException qdsee){
            throw new UnknownQueueException(qdsee);
        }
    }
}
