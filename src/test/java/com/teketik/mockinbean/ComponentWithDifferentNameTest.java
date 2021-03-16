package com.teketik.mockinbean;

import com.teketik.mockinbean.components.MockableComponent1;
import com.teketik.mockinbean.components.TestComponent1;
import com.teketik.test.mockinbean.MockInBean;

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
