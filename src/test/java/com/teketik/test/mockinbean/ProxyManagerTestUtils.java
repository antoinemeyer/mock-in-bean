package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Optional;


public class ProxyManagerTestUtils extends AbstractTestExecutionListener {

    private static ThreadLocal<MockInBeanTracker> MOCK_IN_BEAN_TRACKER_CONTAINER = new ThreadLocal<MockInBeanTracker>();

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        MOCK_IN_BEAN_TRACKER_CONTAINER.set(testContext.getApplicationContext().getBean(MockInBeanTracker.class));
    }

    /**
     * @param proxyCandidate
     * @param mockOrSpy
     * @return true if {@code proxyCandidate} is a proxy of {@code mockOrSpy} (as provided by
     *         {@link MockInBeanTracker}).
     */
    public static boolean isProxyOf(Object proxyCandidate, Object mockOrSpy) {
        final MockInBeanTracker mockInBeanTracker = MOCK_IN_BEAN_TRACKER_CONTAINER.get();
        final Object bean = mockInBeanTracker.proxyTracker.getBeanByProxy(proxyCandidate);
        final Optional<Object> trackedMock = mockInBeanTracker.mockTracker.getTracked(bean);
        if (!trackedMock.isPresent()) {
            return false;
        }
        return trackedMock.get() == mockOrSpy;
    }

}
