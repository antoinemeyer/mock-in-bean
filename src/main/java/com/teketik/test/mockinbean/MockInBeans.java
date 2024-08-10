package com.teketik.test.mockinbean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that aggregates several {@link MockInBean @MockInBean} annotations.
 * <p>
 * Can be used natively, declaring several nested {@link MockInBean @MockInBean} annotations.
 * Can also be used in conjunction with Java 8's support for <em>repeatable
 * annotations</em>, where {@link MockInBean @MockInBean} can simply be declared several times
 * on the same {@linkplain ElementType#TYPE type}, implicitly generating this container
 * annotation.
 *
 * @author Antoine Meyer
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MockInBeans {

    /**
     * @return the contained {@link MockInBean @MockInBean} annotations.
     */
    MockInBean[] value();

}
