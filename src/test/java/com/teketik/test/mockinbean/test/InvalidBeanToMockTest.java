package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeanTestExecutionListenerBridge;
import com.teketik.test.mockinbean.MockInBeans;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponentWith1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;

@TestExecutionListeners(
        value = { InvalidBeanToMockTest.TestExecutionListener.class },
        inheritListeners = false
)
public class InvalidBeanToMockTest extends BaseTest {

    static class TestExecutionListener extends MockInBeanTestExecutionListenerBridge {

        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            try {
                super.beforeTestMethod(testContext);
                Assertions.fail();
            } catch (IllegalArgumentException e) {
                Assertions.assertEquals(
                    "Cannot find bean to mock class com.teketik.test.mockinbean.test.components.MockableComponent1 in class com.teketik.test.mockinbean.test.components.TestComponentWith1",
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

    @MockInBeans({
        @MockInBean(TestComponent1.class),
        @MockInBean(TestComponentWith1.class)
    })
    private MockableComponent1 mockableComponent1;

    @Test
    public void test() {
    }

}
