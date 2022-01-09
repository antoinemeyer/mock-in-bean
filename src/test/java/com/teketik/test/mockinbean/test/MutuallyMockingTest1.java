package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.components.TestComponentWith1;
import com.teketik.test.mockinbean.test.components.TestComponentWith2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

public class MutuallyMockingTest1 extends MutuallyMockingBaseTest {

    @SpyInBean(TestComponentWith1.class)
    private TestComponentWith2 testComponentWith2;

    @Resource
    private TestComponentWith1 testComponentWith1;
    
    @Test
    public void test() throws InterruptedException {
        //wait for all threads to be there to do the same thing together
        COUNTDOWNLATCH.countDown();
        Assertions.assertTrue(COUNTDOWNLATCH.await(2, TimeUnit.SECONDS));
        final AtomicBoolean called = new AtomicBoolean();
        Mockito.doAnswer(a -> {
            called.set(true);
            return null;
        }).when(testComponentWith2).invokeOther();
        testComponentWith1.invokeOther();
        Assertions.assertTrue(called.get());
        Mockito.verify(testComponentWith2).invokeOther();
    }
}
