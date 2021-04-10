package com.teketik.test.mockinbean;

import org.springframework.lang.Nullable;

/**
 * Defines a recipient of a {@link MockDefinition} or {@link SpyDefinition} extracted from {@link MockInBean} or {@link SpyInBean}.
 * @author Antoine Meyer
 */
class InBeanDefinition {

    final Class<?> clazz;

    @Nullable
    final String name;

    InBeanDefinition(Class<?> clazz, String name) {
        super();
        this.clazz = clazz;
        this.name = name;
    }

}
