package com.teketik.mockinbean.components;

import org.springframework.stereotype.Component;

@Component
public class MockableComponent2 {

    public void doSomething() {
        throw new UnsupportedOperationException();
    }

}
