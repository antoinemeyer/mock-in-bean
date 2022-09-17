package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.MockInBeanTestExecutionListenerBridge;
import com.teketik.test.mockinbean.MockInBeans;
import com.teketik.test.mockinbean.test.InvalidBeanDefinitionTest.Config.TestComponentWithInvalidMockableComponent;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;

import javax.annotation.PostConstruct;

@TestExecutionListeners(
        value = { InvalidBeanDefinitionTest.TestExecutionListener.class },
        inheritListeners = false
)
public class InvalidBeanDefinitionTest extends BaseTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {

        @Component
        class TestComponentWithInvalidMockableComponent {

            @Autowired
            private MockableComponent1 mockableComponent1;

            @PostConstruct
            public void alter() {
                mockableComponent1 = new MockableComponent1();
            }
        }
    }

    static class TestExecutionListener extends MockInBeanTestExecutionListenerBridge {

        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            try {
                super.beforeTestMethod(testContext);
                Assertions.fail();
            } catch (IllegalArgumentException e) {
                Assertions.assertEquals(
                    "Resolved invalid target beans for definition Definition [name=mockableComponent1, resolvableType=com.teketik.test.mockinbean.test.components.MockableComponent1] "
                        + "in InBeanDefinition [clazz=class com.teketik.test.mockinbean.test.InvalidBeanDefinitionTest$Config$TestComponentWithInvalidMockableComponent, name=null]",
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
        @MockInBean(TestComponentWithInvalidMockableComponent.class)
    })
    private MockableComponent1 mockableComponent1;

    @Test
    public void test() {
    }

}
