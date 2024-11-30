package com.teketik.test.mockinbean;

import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

abstract class FieldState {

    final Field field;

    final Definition definition;

    public FieldState(Field targetField, Definition definition) {
        this.field = targetField;
        this.definition = definition;
    }

    abstract Object resolveTarget(TestContext testContext);

}
