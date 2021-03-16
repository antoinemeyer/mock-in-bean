package com.teketik.test.mockinbean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that aggregates several {@link SpyInBean @SpyInBean} annotations.
 * <p>
 * Can be used natively, declaring several nested {@link SpyInBean @SpyInBean} annotations.
 * Can also be used in conjunction with Java 8's support for <em>repeatable
 * annotations</em>, where {@link SpyInBean @SpyInBean} can simply be declared several times
 * on the same {@linkplain ElementType#TYPE type}, implicitly generating this container
 * annotation.
 *
 * @author Antoine Meyer
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpyInBeans {

    /**
     * @return the contained {@link SpyInBean} annotations.
     */
    SpyInBean[] value();

}
