package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeanTestExecutionListenerBridge;
import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;

import javax.annotation.Resource;

@TestExecutionListeners(
        value = { MultipleMockDefinitionForAFieldTest.TestExecutionListener.class },
        inheritListeners = false
)
class MultipleMockDefinitionForAFieldTest extends BaseTest {

    static class TestExecutionListener extends MockInBeanTestExecutionListenerBridge {

        @Override
        public void beforeTestClass(TestContext testContext) throws Exception {
            try {
                super.beforeTestClass(testContext);
                Assertions.fail();
            } catch (IllegalArgumentException e) {
                Assertions.assertEquals(
                    "private com.teketik.test.mockinbean.test.components.MockableComponent1 com.teketik.test.mockinbean.test.components.TestComponentBase.mockableComponent1 can only be mapped once, as a mock or a spy, not both!",
                    e.getMessage()
                );
            }
        }

        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            // empty
        }

        @Override
        public void afterTestClass(TestContext testContext) throws Exception {
            // empty
        }

    }

    @MockInBean(TestComponent1.class)
    private MockableComponent1 mockableComponent1a;

    @SpyInBean(TestComponent1.class)
    private MockableComponent1 mockableComponent1b;

    @Resource
    private TestComponent1 testComponent1;

    @Test
    public void test() {
        Assertions.assertNull(mockableComponent1a);
        Assertions.assertNull(mockableComponent1b);
        Assertions.assertNull(testComponent1);
    }

}
