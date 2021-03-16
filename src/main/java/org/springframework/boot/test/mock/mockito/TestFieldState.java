package org.springframework.boot.test.mock.mockito;

import org.springframework.test.context.TestContext;

import java.lang.reflect.Field;

class TestFieldState extends FieldState {

    public TestFieldState(Field targetField, Object originalValue, DefinitionFacade definition) {
        super(targetField, originalValue, definition);
    }

    @Override
    public Object resolveTarget(TestContext testContext) {
        return testContext.getTestInstance();
    }

}
