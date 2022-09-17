package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponentInterface;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

public class TestInterface extends BaseTest {

    @Autowired
    private TestComponentInterface testComponentInterface;

    @MockInBean(TestComponentInterface.class)
    private TestComponent1 testComponent1;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(testComponent1));
        Assertions.assertSame(testComponent1, ReflectionTestUtils.getField(testComponentInterface, "testComponent1"));
    }

}
