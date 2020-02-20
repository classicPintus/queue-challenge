package com.example.test;

import com.example.QueueService;
import com.example.dto.MessageToSend;
import com.example.dto.QueueMessage;
import com.example.exception.InvalidQueueNameException;
import com.example.exception.InvalidVisibilityTimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {

    protected QueueService target;

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
        } catch(Exception e){
            Assert.fail(e.getMessage());
        }
        finally {
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

    @Test
    public void shouldGetValuesInOrder() {
        String queueName = UUID.randomUUID().toString() + ".fifo";
        target.createQueue(queueName, 1);
        try {
            target.push(queueName, new MessageToSend("1"));
            target.push(queueName, new MessageToSend("2"));

            {
                Optional<QueueMessage> message = target.pull(queueName);
                Assert.assertTrue(message.isPresent());
                Assert.assertEquals("1", message.get().getBody());
                Assert.assertNotNull(message.get().getUniqueIdentifier());
                target.delete(queueName, message.get());
            }

            {
                Optional<QueueMessage> message = target.pull(queueName);
                Assert.assertTrue(message.isPresent());
                Assert.assertEquals("2", message.get().getBody());
                Assert.assertNotNull(message.get().getUniqueIdentifier());
                target.delete(queueName, message.get());
            }

            Assert.assertTrue(target.pull(queueName).isEmpty());
        } finally {
            target.deleteQueue(queueName);
        }
    }

    @Test
    public void shouldPutInTheHeadMessageWithExpiredVisibilityTimeout() throws InterruptedException {
        String queueName = UUID.randomUUID().toString() + ".fifo";
        target.createQueue(queueName, 1);
        try {
            target.push(queueName, new MessageToSend("1"));
            target.push(queueName, new MessageToSend("2"));

            {
                Optional<QueueMessage> message = target.pull(queueName);
                Assert.assertTrue(message.isPresent());
                Assert.assertEquals("1", message.get().getBody());
            }

            TimeUnit.MILLISECONDS.sleep(1200); // 1.2 secs

            {
                Optional<QueueMessage> message = target.pull(queueName);
                Assert.assertTrue(message.isPresent());
                Assert.assertEquals("1", message.get().getBody());
            }

        } finally {
            target.deleteQueue(queueName);
        }
    }
}
