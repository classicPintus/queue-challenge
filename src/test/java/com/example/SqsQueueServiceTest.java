package com.example;

import com.example.exception.InvalidQueueNameException;
import com.example.dto.QueueMessage;
import com.example.dto.MessageToSend;
import com.example.exception.InvalidVisibilityTimeoutException;
import com.example.sqs.SqsQueueService;
import com.example.test.BaseUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

// with right permissions this test works
@Ignore
public class SqsQueueServiceTest extends BaseUnitTest {

    private SqsQueueService target;

    @Before
    public void setUp() {
        target = new SqsQueueService();
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

    @Test
    public void shouldRaiseExceptionBecauseTheQueueNameSuffixIsNotFifo() {
        String queueName = "prova";
        Assert.assertThrows(InvalidQueueNameException.class, () -> target.createQueue(queueName, 1));
    }

    @Test
    public void shouldRaiseExceptionBecauseTheQueueNameIsNull() {
        String queueName = null;
        Assert.assertThrows(InvalidQueueNameException.class, () -> target.createQueue(queueName, 1));
    }

    @Test
    public void shouldRaiseExceptionBecauseTheQueueNameIsBiggerThanEightyChars() {
        String queueName = "12345678901234567890123456789012345678901234567890123456789012345678901234567890.fifo";
        Assert.assertThrows(InvalidQueueNameException.class, () -> target.createQueue(queueName, 1));
    }

    @Test
    public void shouldNotThrowAnyErrorWhenTryToCreateQueueWithSameName() {
        String queueName = UUID.randomUUID().toString() + ".fifo";
        target.createQueue(queueName, 1);
        try {
            target.createQueue(queueName, 1);
        } finally {
            target.deleteQueue(queueName);
        }
    }

    @Test
    public void shouldRaiseExceptionBecauseVisibilityTimeoutIsTooLow() {
        String queueName = UUID.randomUUID().toString() + ".fifo";
        Assert.assertThrows(InvalidVisibilityTimeoutException.class, () -> target.createQueue(queueName, -1));
    }

    @Test
    public void shouldRaiseExceptionBecauseVisibilityTimeoutIsTooHigh() {
        String queueName = UUID.randomUUID().toString() + ".fifo";
        Assert.assertThrows(InvalidVisibilityTimeoutException.class, () -> target.createQueue(queueName, 43201));
    }
}
