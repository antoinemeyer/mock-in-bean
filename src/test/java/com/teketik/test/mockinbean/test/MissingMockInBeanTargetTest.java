package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBeans;
import com.teketik.test.mockinbean.test.components.MockableComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MissingMockInBeanTargetTest extends BaseTest {

    @MockInBeans({
        //no target. no mock created/injected
    })
    private MockableComponent1 mockableComponent1;

    @Test
    public void test() {
        Assertions.assertNull(mockableComponent1);
    }

}
