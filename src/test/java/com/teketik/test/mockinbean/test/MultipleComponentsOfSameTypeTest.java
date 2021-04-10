package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponentWithMultipleSameType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

class MultipleComponentsOfSameTypeTest extends BaseTest {

    @MockInBean(TestComponentWithMultipleSameType.class)
    private MockableComponent1 mockableComponent1a;

    @MockInBean(TestComponentWithMultipleSameType.class)
    private MockableComponent1 mockableComponent1b;

    @Resource
    private TestComponentWithMultipleSameType testComponentWithMultipleSameTypeDependencies;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1a));
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1b));
        Assertions.assertSame(mockableComponent1a, ReflectionTestUtils.getField(testComponentWithMultipleSameTypeDependencies, "mockableComponent1a"));
        Assertions.assertSame(mockableComponent1b, ReflectionTestUtils.getField(testComponentWithMultipleSameTypeDependencies, "mockableComponent1b"));
    }

}
