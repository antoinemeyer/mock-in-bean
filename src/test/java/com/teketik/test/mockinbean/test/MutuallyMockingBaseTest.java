package com.teketik.test.mockinbean.test;

abstract class MutuallyMockingBaseTest extends ConcurrentBaseTest {

    final static ConcurrentTestSynchronizer SYNCHRONIZER = new ConcurrentTestSynchronizer(2);

}
