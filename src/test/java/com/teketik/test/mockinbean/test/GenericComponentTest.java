package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.GenericMockableComponent;
import com.teketik.test.mockinbean.test.components.GenericTestComponent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

class GenericComponentTest extends BaseTest {

    @MockInBean(GenericTestComponent.class)
    private GenericMockableComponent<String> genericMockableComponent;

    @Resource
    private GenericTestComponent genericTestComponent;

    @Test
    public void test() {
        Assertions.assertTrue(TestUtils.isMock(genericMockableComponent));
        Assertions.assertSame(genericMockableComponent, ReflectionTestUtils.getField(genericTestComponent, "genericMockableComponent"));
    }

}
