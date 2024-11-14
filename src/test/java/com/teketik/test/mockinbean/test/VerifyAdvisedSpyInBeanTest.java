package com.teketik.test.mockinbean.test;

import static org.mockito.Mockito.verify;

import com.teketik.test.mockinbean.SpyInBean;
import com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.AnAspect;
import com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.LoggingService;
import com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.ProviderService;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Covering test case from https://github.com/inkassso/mock-in-bean-issue-23/blob/master/src/test/java/com/github/inkassso/mockinbean/issue23/service/BrokenLoggingServiceTest1_SpyInBean.java
 */
@TestExecutionListeners(value = {VerifyAdvisedSpyInBeanTest.class}, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@SpringBootTest
public class VerifyAdvisedSpyInBeanTest implements TestExecutionListener, Ordered {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {

        @Aspect
        @Component
        public class AnAspect {

            private final AtomicInteger invocationCounter = new AtomicInteger();

            @Before("execution(* com.teketik.test.mockinbean.test.VerifyAdvisedSpyInBeanTest.Config.ProviderService.provideValue())")
            public void run() {
                invocationCounter.incrementAndGet();
            }
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

            public String logCurrentValue() {
                return providerService.provideValue();
            }
        }
    }

    @Autowired
    protected LoggingService loggingService;

    @Autowired
    protected AnAspect anAspect;

    @SpyInBean(LoggingService.class)
    private ProviderService providerService;

    @Test
    void testAspectInvocation() {
        int initialCounterValue = anAspect.invocationCounter.get();
        loggingService.logCurrentValue();
        Assertions.assertEquals(initialCounterValue + 1, anAspect.invocationCounter.get());
        verify(providerService).provideValue();
        Assertions.assertEquals(initialCounterValue + 2, anAspect.invocationCounter.get());
    }

    @Test
    void testSpyAnswer() {
        Mockito.doAnswer(i -> "value").when(providerService).provideValue();
        Assertions.assertEquals("value", loggingService.logCurrentValue());
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        final ApplicationContext applicationContext = testContext.getApplicationContext();

        //ensure context clean
        final Object loggingServiceBean = applicationContext.getBean(LoggingService.class);
        final Object providerServiceInBean = ReflectionTestUtils.getField(loggingServiceBean, "providerService");
        Assertions.assertFalse(TestUtils.isMockOrSpy(providerServiceInBean));
        Assertions.assertSame(applicationContext.getBean(ProviderService.class), providerServiceInBean);

        //ensure aspect invoked
        final AnAspect anAspect = applicationContext.getBean(AnAspect.class);
        Assertions.assertEquals(4, anAspect.invocationCounter.get());
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
