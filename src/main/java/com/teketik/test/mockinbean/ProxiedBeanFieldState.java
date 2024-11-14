package com.teketik.test.mockinbean;

import org.springframework.aop.TargetSource;
import org.springframework.test.context.TestContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;

/**
 * Special kind of {@link BeanFieldState} handling proxied beans (like aspects).<br>
 * The mock is not injected into the <code>field</code> but into the <code>target</code> of its {@link TargetSource}.
 * @author Antoine Meyer
 * @see https://github.com/antoinemeyer/mock-in-bean/issues/23
 */
class ProxiedBeanFieldState extends BeanFieldState {

    private static void setTargetSourceValue(TargetSource targetSource, Object value) {
        ReflectionTestUtils.setField(targetSource, "target", value);
    }

    final TargetSource proxyTargetSource;

    final Object proxyTargetOriginalValue;

    public ProxiedBeanFieldState(Object inBean, Field beanField, Object beanFieldValue, TargetSource proxyTargetSource, Definition definition) throws Exception {
        super(inBean, beanField, beanFieldValue, definition);
        this.proxyTargetSource = proxyTargetSource;
        this.proxyTargetOriginalValue = proxyTargetSource.getTarget();
    }

    @Override
    public void rollback(TestContext testContext) {
        setTargetSourceValue(proxyTargetSource, proxyTargetOriginalValue);
    }

    @Override
    public Object createMockOrSpy() {
        Object applicableMockOrSpy = definition.create(proxyTargetOriginalValue);
        setTargetSourceValue(proxyTargetSource, applicableMockOrSpy);
        return originalValue; //the 'mock or spy' to operate for proxied beans are the actual proxy
    }

}
