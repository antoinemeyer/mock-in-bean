package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.BeanUtils;
import com.teketik.test.mockinbean.MockInBeanTestExecutionListenerConfig;

import org.junit.jupiter.api.Assertions;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.util.AopTestUtils;
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
            final Class<? extends Object> beanClass = AopTestUtils.getTargetObject(bean).getClass();
            if (beanClass.getName().startsWith("com.teketik.test.mockinbean.test.components")) {
                ReflectionUtils.doWithFields(beanClass, field -> {
                    if (!field.isSynthetic()) {
                        field.setAccessible(true);
                        final Object objectInBean = field.get(bean);
                        if (objectInBean != null) {
                            verifyObject(
                                applicationContext,
                                objectInBean
                            );
                        }
                        verifiedCounter.getAndIncrement();
                    }
                });
            }
        }
        Assertions.assertTrue(verifiedCounter.get() >= 10);
    }

    private void verifyObject(final ApplicationContext applicationContext, final Object objectInBean) {
        Assertions.assertFalse(TestUtils.isMockOrSpy(objectInBean));
        if (!isRunningConcurrentBuild(applicationContext)) {
            /*
             * if the tests are NOT running concurrently, we can ensure that the proxies are always rolled back
             * from the beans.
             */
            final String objectInBeanName = BeanUtils.findBeanName(objectInBean, applicationContext).get();
            Assertions.assertSame(objectInBean, applicationContext.getBean(objectInBeanName));
        }
    }

    private boolean isRunningConcurrentBuild(ApplicationContext applicationContext) {
        final String concurrentProperty = applicationContext.getEnvironment().getProperty("concurrent");
        return concurrentProperty != null && Boolean.parseBoolean(concurrentProperty);
    }

    @Override
    public int getOrder() {
        return MockInBeanTestExecutionListenerConfig.ORDER - 1;
    }

}
