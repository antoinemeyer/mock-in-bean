package com.teketik.mockinbean;

import org.junit.jupiter.api.Assertions;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Ensures that the context is clean after the test (by ensuring none of the spring beans contain a mock or a spy).
 * @author Antoine Meyer
 */
class AssertingCleanTestExecutionListener implements TestExecutionListener, Ordered {

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        final AtomicInteger verifiedCounter = new AtomicInteger();
        final ApplicationContext applicationContext = testContext.getApplicationContext();
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            final Object bean = applicationContext.getBean(beanName);
            if (bean.getClass().getName().startsWith("com.teketik.mockinbean.components")) {
                ReflectionUtils.doWithFields(bean.getClass(), field -> {
                    field.setAccessible(true);
                    Assertions.assertFalse(TestUtils.isMockOrSpy(field.get(bean)));
                    verifiedCounter.getAndIncrement();
                });
            }
        }
        Assertions.assertTrue(verifiedCounter.get() >= 10);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
