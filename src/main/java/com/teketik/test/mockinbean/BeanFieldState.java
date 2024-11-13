package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

class BeanFieldState extends FieldState {

    final Object bean;

    final Object originalValue;

    public BeanFieldState(Object bean, Field field, Object originalValue, Definition definition) {
        super(field, definition);
        this.bean = bean;
        this.originalValue = originalValue;
    }

    @Override
    public Object resolveTarget(TestContext testContext) {
        return bean;
    }

    public void rollback(TestContext testContext) {
        final Object target = resolveTarget(testContext);
        ReflectionUtils.setField(field, target, originalValue);
    }

    public Object createMockOrSpy() {
        return definition.create(originalValue);
    }

}
