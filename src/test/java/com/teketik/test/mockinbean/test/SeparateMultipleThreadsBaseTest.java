package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.InterceptedComponent;
import com.teketik.test.mockinbean.test.components.MockableComponent1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

abstract class SeparateMultipleThreadsBaseTest extends ConcurrentBaseTest {

    @MockInBean(InterceptedComponent.class)
    private MockableComponent1 mockableComponent1;

    @Resource
    private InterceptedComponent interceptedComponent;

    final static CountDownLatch COUNTDOWNLATCH = new CountDownLatch(2);
    final static AtomicBoolean EXCEPTION_VERIFIER = new AtomicBoolean();

    @Test
    public void test() throws Exception {
        //wait for all threads to be there to do the same thing together
        COUNTDOWNLATCH.countDown();
        Assertions.assertTrue(COUNTDOWNLATCH.await(2, TimeUnit.SECONDS));

        /*
         * assert that the operation cannot be processed:
         * there are two tests that run an operation with that mock on a different thread.
         * the proxy cannot guarantee for sure which is which, so both fail.
         */
        try {
            Executors.newSingleThreadExecutor().submit(() -> {
                interceptedComponent.process();
            }).get(2, TimeUnit.HOURS);
        } catch (Exception e) {
            EXCEPTION_VERIFIER.set(true);
        }
    }

    @AfterAll
    public static void afterAll() {
        Assertions.assertTrue(EXCEPTION_VERIFIER.get());
    }

}
