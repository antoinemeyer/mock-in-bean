package com.teketik.test.mockinbean.test.components;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InterceptedComponentAspect {

    @Before("execution(* com.teketik.test.mockinbean.test.components.InterceptedComponent.*(..))")
    public void intercept() {
    }

}