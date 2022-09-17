package com.teketik.test.mockinbean;

import org.springframework.test.context.support.AbstractTestExecutionListener;

public final class MockInBeanTestExecutionListenerConfig {

    private MockInBeanTestExecutionListenerConfig() {}

    /**
     * {@link MockInBeanTestExecutionListener} {@link AbstractTestExecutionListener#getOrder() order}.
     */
    public static final int ORDER = 10_000;

}
