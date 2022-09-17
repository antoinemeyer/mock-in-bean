package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.components.TestComponentWith1;
import com.teketik.test.mockinbean.test.components.TestComponentWith2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

public class MutuallyMocking2Test extends MutuallyMockingBaseTest {

    @SpyInBean(TestComponentWith2.class)
    private TestComponentWith1 testComponentWith1;

    @Resource
    private TestComponentWith2 testComponentWith2;

    @Test
    public void test() throws InterruptedException {
        //wait for all threads to be there to do the same thing together
        SYNCHRONIZER.await();
        final AtomicBoolean called = new AtomicBoolean();
        Mockito.doAnswer(a -> {
            called.set(true);
            return null;
        }).when(testComponentWith1).invokeOther();
        testComponentWith2.invokeOther();
        Assertions.assertTrue(called.get());
        Mockito.verify(testComponentWith1).invokeOther();
    }
}
