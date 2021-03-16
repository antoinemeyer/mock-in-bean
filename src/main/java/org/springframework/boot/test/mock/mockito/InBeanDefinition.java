package org.springframework.boot.test.mock.mockito;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.SpyInBean;

import org.springframework.lang.Nullable;

/**
 * Defines a recipient of a {@link MockDefinition} or {@link SpyDefinition} extracted from {@link MockInBean} or {@link SpyInBean}.
 * @author Antoine Meyer
 */
class InBeanDefinition {

    final Class<?> clazz;

    @Nullable
    final String name;

    public InBeanDefinition(Class<?> clazz, String name) {
        super();
        this.clazz = clazz;
        this.name = name;
    }

}
