package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

class TestFieldState extends FieldState {

    TestFieldState(Field targetField, Object originalValue, Definition definition) {
        super(targetField, originalValue, definition);
    }

    @Override
    Object resolveTarget(TestContext testContext) {
        return testContext.getTestInstance();
    }

}
