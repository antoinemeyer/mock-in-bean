package com.teketik.test.mockinbean.test;

abstract class MutuallyMockingBaseTest extends ConcurrentBaseTest {

    final static ConcurrentTestWaiter SYNCHRONIZER = new ConcurrentTestWaiter(2);

}
