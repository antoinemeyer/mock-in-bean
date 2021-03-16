package org.springframework.boot.test.mock.mockito;

import org.springframework.lang.Nullable;
import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

abstract class FieldState {

    final Field field;

    @Nullable
    final Object originalValue;

    final DefinitionFacade definition;

    public FieldState(Field targetField, Object originalValue, DefinitionFacade definition) {
        this.field = targetField;
        this.originalValue = originalValue;
        this.definition = definition;
    }

    public abstract Object resolveTarget(TestContext testContext);

}
