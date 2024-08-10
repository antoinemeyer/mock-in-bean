package com.teketik.test.mockinbean.test.annotations;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MockInBean(TestComponent1.class)
public @interface MockInBeanWithCustomName {
}
