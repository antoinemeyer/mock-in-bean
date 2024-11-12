package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

class BeanFieldState extends FieldState {

    private Object bean;

    private Object originalValue;

    private Object mockableValue;

    public BeanFieldState(Object bean, Field field, Object originalValue, Object mockableValue, Definition definition) {
        super(field, definition);
        this.bean = bean;
        this.originalValue = originalValue;
        this.mockableValue = mockableValue;
    }

    @Override
    public Object resolveTarget(TestContext testContext) {
        return bean;
    }

    public Object getMockableValue() {
        return mockableValue;
    }

    public Object getOriginalValue() {
        return originalValue;
    }
}
