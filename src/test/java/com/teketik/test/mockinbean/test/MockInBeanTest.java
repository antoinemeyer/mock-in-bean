package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeans;
import com.teketik.test.mockinbean.ProxyManagerTestUtils;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.MockableComponent2;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class MockInBeanTest extends MockInBeanBaseTest {

    @MockInBeans({
        @MockInBean(TestComponent1.class),
        @MockInBean(TestComponent2.class)
    })
    private MockableComponent1 mockableComponent1;

    @MockInBean(TestComponent1.class)
    private MockableComponent2 mockableComponent2;

    /**
     * Note: Will fail if run individually, run as a class.
     */
    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1));
        Object t1m1 = ReflectionTestUtils.getField(testComponent1, "mockableComponent1");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(t1m1, mockableComponent1));
        Object t2m1 = ReflectionTestUtils.getField(testComponent2, "mockableComponent1");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(t2m1, mockableComponent1));
        Assertions.assertSame(t1m1, t2m1);

        Assertions.assertTrue(TestUtils.isMock(mockableComponent2));
        Object t1m2 = ReflectionTestUtils.getField(testComponent1, "mockableComponent2");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(t1m2, mockableComponent2));
        Object t2m2 = ReflectionTestUtils.getField(testComponent2, "mockableComponent2");
        Assertions.assertNotSame(t1m2, t2m2);
        Assertions.assertFalse(TestUtils.isMockOrSpy(t2m2));
    }

    @Override
    MockableComponent1 getMockableComponent1() {
        return mockableComponent1;
    }

    @Override
    MockableComponent2 getMockableComponent2() {
        return mockableComponent2;
    }

}
