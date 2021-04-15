package com.teketik.test.mockinbean.test.components;

import org.springframework.stereotype.Component;

@Component
public class MockableComponent1 {

    public void doSomething() {
        throw new UnsupportedOperationException();
    }

}
