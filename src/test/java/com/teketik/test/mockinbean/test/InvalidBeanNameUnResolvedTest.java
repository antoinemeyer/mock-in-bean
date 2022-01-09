package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeanTestExecutionListenerBridge;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;

@TestExecutionListeners(
        value = { InvalidBeanNameUnResolvedTest.TestExecutionListener.class },
        inheritListeners = false
)
class InvalidBeanNameUnResolvedTest extends BaseTest {

    static class TestExecutionListener extends MockInBeanTestExecutionListenerBridge {

        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            try {
                super.beforeTestMethod(testContext);
                Assertions.fail();
            } catch (IllegalArgumentException e) {
                Assertions.assertEquals(
                    "No beans of type class com.teketik.test.mockinbean.test.components.TestComponent3 and name invalid name",
                    e.getMessage()
                );
            }
        }

        @Override
        public void afterTestMethod(TestContext testContext) throws Exception {
            //empty
        }

        @Override
        public void afterTestClass(TestContext testContext) throws Exception {
            // empty
        }

    }

    @MockInBean(value = TestComponent3.class, name = "invalid name")
    private MockableComponent1 mockableComponent1;

    @Autowired
    private TestComponent1 testComponent1;

    @Test
    public void test() {
        Assertions.assertNull(mockableComponent1);
        Assertions.assertNull(testComponent1);
    }

}
