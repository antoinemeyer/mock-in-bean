package com.teketik.mockinbean;

import com.teketik.mockinbean.components.MockableComponent1;
import com.teketik.mockinbean.components.TestComponent3;
import com.teketik.mockinbean.components.TestComponentOn3;
import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

class MultipleComponentsThroughInheritanceTest extends BaseTest {

    @MockInBeans({
        @MockInBean(TestComponentOn3.class),
        @MockInBean(value = TestComponent3.class, name = "testComponent3"),
    })
    private MockableComponent1 mockableComponent1;

    @Resource
    private TestComponentOn3 testComponentOn3;

    @Resource
    private TestComponent3 testComponent3;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1));
        Assertions.assertSame(mockableComponent1, ReflectionTestUtils.getField(testComponentOn3, "mockableComponent1"));
        Assertions.assertSame(mockableComponent1, ReflectionTestUtils.getField(testComponent3, "mockableComponent1"));
        Assertions.assertFalse(TestUtils.isMockOrSpy(ReflectionTestUtils.getField(testComponentOn3, "mockableComponent2")));
        Assertions.assertFalse(TestUtils.isMockOrSpy(ReflectionTestUtils.getField(testComponent3, "mockableComponent2")));
    }

}
