package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.MockInSpyTest.Config.TestComponent1Wrapper;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Resource;

class MockInSpyTest extends BaseTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {

        @Component
        class TestComponent1Wrapper {

            @Resource
            private TestComponent1 testComponent1;

            void doWith1() {
                testComponent1.doWith1();
            }

            void doWith2() {
                testComponent1.doWith2();
            }

        }
    }

    @Resource
    private TestComponent1Wrapper testComponent1Wrapper;

    @SpyInBean(TestComponent1Wrapper.class)
    private TestComponent1 testComponent1;

    @MockInBean(TestComponent1.class)
    private MockableComponent1 mockableComponent1;

    @Test
    public void testMocked1() {
        Mockito.doNothing().when(mockableComponent1).doSomething();
        testComponent1Wrapper.doWith1();
        Mockito.verify(mockableComponent1).doSomething();
    }

    @Test
    public void testMocked2() {
        final RuntimeException runtimeException = new RuntimeException();
        Mockito.doThrow(runtimeException).when(mockableComponent1).doSomething();
        try {
            testComponent1Wrapper.doWith1();
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof InvocationTargetException);
            final Throwable targetException = ((InvocationTargetException) e).getTargetException();
            Assertions.assertEquals(runtimeException, targetException.getCause());
        }
    }

    @Test
    public void testNotMocked() {
        try {
            testComponent1Wrapper.doWith2();
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof InvocationTargetException);
            final Throwable targetException = ((InvocationTargetException) e).getTargetException();
            Assertions.assertEquals(UnsupportedOperationException.class, targetException.getClass());
        }
    }

}
