package com.teketik.test.mockinbean;

import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

class SpyDefinition extends Definition {

    SpyDefinition(String name, ResolvableType type) {
        super(name, type);
    }

    @Override
    <T> T create(Object originalValue) {
        Assert.notNull(originalValue, "originalValue must not be null");
        Assert.isInstanceOf(this.resolvableType.resolve(), originalValue);
        Assert.state(!Mockito.mockingDetails(originalValue).isSpy(), "originalValue is already a spy");
        MockSettings settings = MockReset.withSettings(MockReset.AFTER);
        settings.name(name);
        settings.spiedInstance(originalValue);
        settings.defaultAnswer(Mockito.CALLS_REAL_METHODS);
        return (T) Mockito.mock(originalValue.getClass(), settings);
    }

}
