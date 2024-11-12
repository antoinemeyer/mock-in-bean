package com.teketik.test.mockinbean.test;

import static org.mockito.Mockito.verify;

import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.LoggingService;
import com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.ProviderService;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Covering test case from
 * https://github.com/inkassso/mock-in-bean-issue-23/blob/master/src/test/java/com/github/inkassso/mockinbean/issue23/service/BrokenLoggingServiceTest1_SpyInBean.java
 */
@TestExecutionListeners(value = {VerifyAdvisedSpyInBeanTest.class}, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@SpringBootTest
public class VerifyAdvisedSpyInBeanTest implements TestExecutionListener, Ordered {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {

        @Aspect
        @Component
        public class AnAspect {
            @Before("execution(* com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.ProviderService.provideValue())")
            public void logBeforeMethodExecution() {}
        }

        @Service
        public class ProviderService {
            public String provideValue() {
                return "";
            }
        }

        @Component
        public class LoggingService {

            @Autowired
            private ProviderService providerService;

            public void logCurrentValue() {
                providerService.provideValue();
            }
        }
    }

    @Autowired
    protected LoggingService loggingService;

    @SpyInBean(LoggingService.class)
    private ProviderService providerService;

    @Test
    void testLogCurrentValue() {
        loggingService.logCurrentValue();
        verify(providerService).provideValue();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        final ApplicationContext applicationContext = testContext.getApplicationContext();
        final Object loggingServiceBean = applicationContext.getBean(LoggingService.class);
        Assertions.assertSame(applicationContext.getBean(ProviderService.class), ReflectionTestUtils.getField(loggingServiceBean, "providerService"));
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
