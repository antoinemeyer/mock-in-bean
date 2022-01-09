package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;


public class ProxyManagerTestUtils extends AbstractTestExecutionListener {

    private static ThreadLocal<MockInBeanTracker> MOCK_IN_BEAN_TRACKER_CONTAINER = new ThreadLocal<MockInBeanTracker>();

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        MOCK_IN_BEAN_TRACKER_CONTAINER.set(testContext.getApplicationContext().getBean(MockInBeanTracker.class));
    }

    /**
     * @param proxyCandidate
     * @param object
     * @return true if {@code proxyCandidate} is a proxy of {@code object} (as provided by
     *         {@link MockInBeanTracker}).
     */
    public static boolean isProxyOf(Object proxyCandidate, Object mockOrSpy) {
        final MockInBeanTracker mockInBeanTracker = MOCK_IN_BEAN_TRACKER_CONTAINER.get();
        final String beanName = mockInBeanTracker.proxyTracker.getNameByProxy(proxyCandidate);
        final Object mockInThreadLocal = mockInBeanTracker.mockTracker.getTracked(beanName).get();
        return mockInThreadLocal == mockOrSpy;
    }

}
