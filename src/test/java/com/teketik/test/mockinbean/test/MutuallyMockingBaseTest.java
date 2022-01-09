package com.teketik.test.mockinbean.test;

import java.util.concurrent.CountDownLatch;

abstract class MutuallyMockingBaseTest extends ConcurrentBaseTest {

    final static CountDownLatch COUNTDOWNLATCH = new CountDownLatch(2);

}
