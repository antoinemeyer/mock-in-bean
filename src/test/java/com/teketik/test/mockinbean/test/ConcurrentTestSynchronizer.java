package com.teketik.test.mockinbean.test;

import org.junit.jupiter.api.Assertions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConcurrentTestSynchronizer {

    private final CountDownLatch countDownLatch;

    public ConcurrentTestSynchronizer(int count) {
        this.countDownLatch = new CountDownLatch(count);
    }

    public void await() {
        countDownLatch.countDown();
        try {
            Assertions.assertTrue(countDownLatch.await(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
