package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.MockableComponent2;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;


@TestExecutionListeners(
        value = { MockInBeanBaseTest.TestExecutionListener.class }
)
abstract class MockInBeanBaseTest extends BaseTest {

    static class TestExecutionListener extends AbstractTestExecutionListener {

        private static MockableComponent1 mockableComponent1firstTest;
        private static MockableComponent2 mockableComponent2firstTest;
        private static boolean multiTestChecked;

        @Override
        public void afterTestMethod(TestContext testContext) throws Exception {
            final MockInBeanBaseTest baseTest = (MockInBeanBaseTest) testContext.getTestInstance();
            if (mockableComponent1firstTest == null && mockableComponent2firstTest == null) {
                mockableComponent1firstTest = baseTest.getMockableComponent1();
                mockableComponent2firstTest = baseTest.getMockableComponent2();
            } else {
                Assertions.assertNotSame(baseTest.getMockableComponent1(), mockableComponent1firstTest);
                Assertions.assertNotSame(baseTest.getMockableComponent2(), mockableComponent2firstTest);
                multiTestChecked = true;
            }
        }

        @Override
        public void afterTestClass(TestContext testContext) throws Exception {
            Assertions.assertTrue(multiTestChecked);
        }
    }

    @Autowired
    protected TestComponent1 testComponent1;

    @Autowired
    protected TestComponent2 testComponent2;

    @Test
    public void emptyTestForMockRecreationVerification() {
    }

    abstract MockableComponent1 getMockableComponent1();

    abstract MockableComponent2 getMockableComponent2();

}
