package com.example.test;

import com.example.QueueService;
import com.example.exception.InvalidQueueNameException;
import com.example.exception.InvalidVisibilityTimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class BaseUnitTest {

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
