package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.test.annotations.MockInBeanWithCustomName;
import com.teketik.test.mockinbean.test.annotations.MockInMultipleComponents;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.MockableComponent2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class MockInBeanMetaAnnotationsTest extends MockInBeanBaseTest {

    @MockInMultipleComponents
    private MockableComponent1 mockableComponent1;

    @MockInBeanWithCustomName
    private MockableComponent2 mockableComponent2;

    /**
     * Note: Will fail if run individually, run as a class.
     */
    @Test
    void test() {
        Assertions.assertTrue(TestUtils.isMock(mockableComponent1));
        Assertions.assertSame(mockableComponent1, ReflectionTestUtils.getField(testComponent1, "mockableComponent1"));
        Assertions.assertSame(mockableComponent1, ReflectionTestUtils.getField(testComponent2, "mockableComponent1"));

        Assertions.assertTrue(TestUtils.isMock(mockableComponent2));
        Assertions.assertSame(mockableComponent2, ReflectionTestUtils.getField(testComponent1, "mockableComponent2"));
        Assertions.assertNotSame(mockableComponent2, ReflectionTestUtils.getField(testComponent2, "mockableComponent2"));
        Assertions.assertFalse(TestUtils.isMockOrSpy(ReflectionTestUtils.getField(testComponent2, "mockableComponent2")));
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
