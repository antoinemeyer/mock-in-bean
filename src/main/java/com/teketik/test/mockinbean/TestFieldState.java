package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

class TestFieldState extends FieldState {

    TestFieldState(Field targetField, Definition definition) {
        super(targetField, definition);
    }

    @Override
    Object resolveTarget(TestContext testContext) {
        return testContext.getTestInstance();
    }

}
