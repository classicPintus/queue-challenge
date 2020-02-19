package com.example;

import com.example.dto.MessageToSend;
import com.example.dto.QueueMessage;
import com.example.sqs.SqsQueueService;
import com.example.test.BaseUnitTest;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

// with right permissions this test works
@Ignore
public class SqsQueueServiceTest extends BaseUnitTest {

    @Before
    public void setUp() {
        target = new SqsQueueService(new QueueNameValidator(), new VisibilityTimeoutValidator());
    }

    @Test
    public void testWholeWorkflow() {

        String queueName = UUID.randomUUID().toString() + ".fifo";
        target.createQueue(queueName, 1);

        try {

            target.push(queueName, new MessageToSend("prova"));

            Optional<QueueMessage> messageReceived = target.pull(queueName);
            Assert.assertTrue(messageReceived.isPresent());
            Assert.assertEquals("prova", messageReceived.get().getBody());
            Assert.assertNotNull(messageReceived.get().getUniqueIdentifier());

            target.delete(queueName, messageReceived.get());
        } finally {
            target.deleteQueue(queueName);
        }
    }
}
