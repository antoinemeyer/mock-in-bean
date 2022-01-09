package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.ProxyManagerTestUtils;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class InvalidBeanNameResolvedByTypeTest extends BaseTest {

    @MockInBean(value = TestComponent1.class, name = "not a valid name")
    private MockableComponent1 mockableComponent1;

    @Autowired
    protected TestComponent1 testComponent1;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1));
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(ReflectionTestUtils.getField(testComponent1, "mockableComponent1"), mockableComponent1));
    }

}
