package com.teketik.test.mockinbean;

import org.springframework.lang.Nullable;
import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

abstract class FieldState {

    final Field field;

    @Nullable
    final Object originalValue;

    final Definition definition;

    public FieldState(Field targetField, Object originalValue, Definition definition) {
        this.field = targetField;
        this.originalValue = originalValue;
        this.definition = definition;
    }

    abstract Object resolveTarget(TestContext testContext);

}
