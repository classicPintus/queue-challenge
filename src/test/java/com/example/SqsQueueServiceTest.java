package com.example;

import com.example.sqs.SqsQueueService;
import com.example.test.BaseUnitTest;
import com.example.utils.QueueNameValidator;
import com.example.utils.VisibilityTimeoutValidator;
import org.junit.Before;
import org.junit.Ignore;

// with right permissions this test works
@Ignore
public class SqsQueueServiceTest extends BaseUnitTest {

    @Before
    public void setUp() {
        target = new SqsQueueService(new QueueNameValidator(), new VisibilityTimeoutValidator());
    }
}
