package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.ProxyManagerTestUtils;
import com.teketik.test.mockinbean.test.components.MockableComponent2;
import com.teketik.test.mockinbean.test.components.TestComponentWithMultipleSameType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

class MultipleComponentsOfSameTypeTest extends BaseTest {

    @MockInBean(TestComponentWithMultipleSameType.class)
    private MockableComponent2 mockableComponentExtending2;

    @MockInBean(TestComponentWithMultipleSameType.class)
    private MockableComponent2 mockableComponent2;
    
    @Resource
    private TestComponentWithMultipleSameType testComponentWithMultipleSameTypeDependencies;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponentExtending2));
        Assertions.assertTrue(TestUtils.isMock(mockableComponent2));
        Object componentMc1a = ReflectionTestUtils.getField(testComponentWithMultipleSameTypeDependencies, "mockableComponentExtending2");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(componentMc1a, mockableComponentExtending2));
        Object componentMc1b = ReflectionTestUtils.getField(testComponentWithMultipleSameTypeDependencies, "mockableComponent2");
        Assertions.assertTrue(ProxyManagerTestUtils.isProxyOf(componentMc1b, mockableComponent2));
        Assertions.assertNotSame(mockableComponentExtending2, mockableComponent2);
        Assertions.assertNotSame(componentMc1a, componentMc1b);
    }

}
