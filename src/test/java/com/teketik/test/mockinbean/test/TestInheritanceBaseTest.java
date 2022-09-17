package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeans;
import com.teketik.test.mockinbean.ProxyManagerTestUtils;
import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.MockableComponent2;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

abstract class TestInheritanceBaseTest extends BaseTest {

    @MockInBeans({
        @MockInBean(TestComponent1.class),
        @MockInBean(TestComponent2.class)
    })
    protected MockableComponent1 mockableComponent1;

    @SpyInBean(TestComponent1.class)
    protected MockableComponent2 mockableComponent2;

    @Resource
    private TestComponent1 testComponent1;

    @Resource
    private TestComponent2 testComponent2;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1));
        Object t1m1 = ReflectionTestUtils.getField(testComponent1, "mockableComponent1");

        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(t1m1, mockableComponent1));
        Object t2m1 = ReflectionTestUtils.getField(testComponent2, "mockableComponent1");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(t2m1, mockableComponent1));
        Assertions.assertSame(t1m1, t2m1);

        Assertions.assertTrue(TestUtils.isSpy(mockableComponent2));
        Object t1m2 = ReflectionTestUtils.getField(testComponent1, "mockableComponent2");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(t1m2, mockableComponent2));
        Object t2m2 = ReflectionTestUtils.getField(testComponent2, "mockableComponent2");
        Assertions.assertFalse(ProxyManagerTestUtils.isProxyOf(t2m2, mockableComponent2));
        Assertions.assertNotSame(t1m2, t2m2);
        Assertions.assertFalse(TestUtils.isMockOrSpy(t2m2));
    }

}
