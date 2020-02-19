package com.example;

import com.example.exception.InvalidQueueNameException;
import com.example.message.MessageReceived;
import com.example.message.MessageToSend;
import com.example.test.BaseUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

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
        String queueName = "prova.fifo";
        target.createQueue(queueName);

        try {

            target.push(queueName, new MessageToSend("prova"));

            Optional<MessageReceived> messageReceived = target.pull(queueName);
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
        Assert.assertThrows(InvalidQueueNameException.class, () -> target.createQueue(queueName));
    }

    @Test
    public void shouldRaiseExceptionBecauseTheQueueNameIsNull() {
        String queueName = null;
        Assert.assertThrows(InvalidQueueNameException.class, () -> target.createQueue(queueName));
    }

    @Test
    public void shouldRaiseExceptionBecauseTheQueueNameIsBiggerThanEightyChars() {
        String queueName = "12345678901234567890123456789012345678901234567890123456789012345678901234567890.fifo";
        Assert.assertThrows(InvalidQueueNameException.class, () -> target.createQueue(queueName));
    }

    @Test
    public void shouldNotThrowAnyErrorWhenTryToCreateQueueWithSameName() {
        String queueName = "prova.fifo";
        target.createQueue(queueName);
        try {
            target.createQueue(queueName);
        } finally {
            target.deleteQueue(queueName);
        }
    }
}
