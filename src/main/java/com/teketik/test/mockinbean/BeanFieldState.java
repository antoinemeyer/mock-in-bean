package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

class BeanFieldState extends FieldState {

    private Object bean;

    public BeanFieldState(Object bean, Field field, Object originalValue, Definition definition) {
        super(field, originalValue, definition);
        this.bean = bean;
    }

    @Override
    public Object resolveTarget(TestContext testContext) {
        return bean;
    }

}
