package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.InterceptedComponent;
import com.teketik.test.mockinbean.test.components.MockableComponent1;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

abstract class SeparateMultipleThreadsBaseTest extends ConcurrentBaseTest {

    @MockInBean(InterceptedComponent.class)
    private MockableComponent1 mockableComponent1;

    @Resource
    private InterceptedComponent interceptedComponent;

    final static ConcurrentTestSynchronizer BEFORE_TEST_WAITER = new ConcurrentTestSynchronizer(2);
    final static ConcurrentTestSynchronizer AFTER_TEST_WAITER = new ConcurrentTestSynchronizer(2);

    @Test
    public void test() throws Exception {
        //wait for all threads to be there to do the same thing together
        BEFORE_TEST_WAITER.await();

        /*
         * assert that the operation cannot be processed:
         * there are two tests that run an operation with that mock on a different thread.
         * the proxy cannot guarantee for sure which is which, so both fail.
         */
        try {
            Executors.newSingleThreadExecutor().submit(() -> {
                interceptedComponent.process();
            }).get(2, TimeUnit.SECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertEquals(UndeclaredThrowableException.class, e.getCause().getClass());
            final Throwable undeclaredThrowable = ((UndeclaredThrowableException) e.getCause()).getUndeclaredThrowable();
            final Throwable targetException = ((InvocationTargetException) undeclaredThrowable).getTargetException();
            Assert.assertEquals(UnsupportedOperationException.class, targetException.getClass());
        } finally {
            AFTER_TEST_WAITER.await(); //We do not want to finish too early and cleanup the proxy before the other thread has processed the assertions!
        }
    }

}
