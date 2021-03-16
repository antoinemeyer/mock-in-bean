package com.teketik.mockinbean.components;

import org.springframework.stereotype.Component;

@Component
public class MockableComponent1 {

    public void doSomething() {
        throw new UnsupportedOperationException();
    }

}
