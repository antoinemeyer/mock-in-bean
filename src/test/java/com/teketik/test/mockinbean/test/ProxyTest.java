package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.InterceptedComponent;
import com.teketik.test.mockinbean.test.components.MockableComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

class ProxyTest extends BaseTest {

    @MockInBean(InterceptedComponent.class)
    private MockableComponent1 mockableComponent1;

    @Resource
    private InterceptedComponent interceptedComponent;

    @Test
    public void test() {
        final AtomicBoolean called = new AtomicBoolean();
        Mockito.doAnswer(a -> {
            called.set(true);
            return null;
        }).when(mockableComponent1).doSomething();
        interceptedComponent.process();
        Assertions.assertTrue(called.get());
    }

}
