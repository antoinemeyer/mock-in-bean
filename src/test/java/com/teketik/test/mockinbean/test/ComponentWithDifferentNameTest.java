package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

class ComponentWithDifferentNameTest extends BaseTest {

    @MockInBean(TestComponent1.class)
    private MockableComponent1 differentName;

    @Resource
    private TestComponent1 testComponent1;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(differentName));
        Assertions.assertSame(differentName, ReflectionTestUtils.getField(testComponent1, "mockableComponent1"));
    }

}
