package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.InterceptedComponent;
import com.teketik.test.mockinbean.test.components.MockableComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

class ConcurrentTest extends ConcurrentBaseTest {

    @MockInBean(InterceptedComponent.class)
    private MockableComponent1 mockableComponent1;

    @Resource
    private InterceptedComponent interceptedComponent;

    private final static ConcurrentTestSynchronizer SYNCHRONIZER = new ConcurrentTestSynchronizer(6);

    @Test
    public void test1() throws InterruptedException {
        test();
    }

    @Test
    public void test2() throws InterruptedException {
        test();
    }

    @Test
    public void test3() throws InterruptedException {
        test();
    }

    @Test
    public void test4() throws InterruptedException {
        test();
    }

    @Test
    public void test5() throws InterruptedException {
        test();
    }

    @Test
    public void test6() throws InterruptedException {
        test();
    }

    private void test() throws InterruptedException {
        //wait for all threads to be there to do the same thing together
        SYNCHRONIZER.await();
        final AtomicBoolean called = new AtomicBoolean();
        Mockito.doAnswer(a -> {
            called.set(true);
            return null;
        }).when(mockableComponent1).doSomething();
        interceptedComponent.process();
        Assertions.assertTrue(called.get());
        Mockito.verify(mockableComponent1).doSomething();
    }

}
