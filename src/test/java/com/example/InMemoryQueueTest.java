package com.example;

import com.example.memory.InMemoryQueueService;
import com.example.test.BaseUnitTest;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;
import org.junit.Before;

public class InMemoryQueueTest extends BaseUnitTest {

    @Before
    public void setUp() {
        target = new InMemoryQueueService(new QueueNameValidator(), new VisibilityTimeoutValidator());
    }
}
