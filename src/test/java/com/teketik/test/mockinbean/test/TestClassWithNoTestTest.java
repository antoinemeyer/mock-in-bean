package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.InterceptedComponent;
import com.teketik.test.mockinbean.test.components.MockableComponent1;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

public class TestClassWithNoTestTest extends BaseTest {

    @MockInBean(InterceptedComponent.class)
    private MockableComponent1 mockableComponent1;
    
    @Resource
    private InterceptedComponent interceptedComponent;

    @Disabled //disabled on purpose
    @Test
    public void cleanupTest() {
    }
}
