package com.teketik.test.mockinbean.test.components;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class TestComponentBase {

    @Autowired
    private MockableComponent1 mockableComponent1;

    @Autowired
    private MockableComponent2 mockableComponent2;

    public void doWith1() {
        mockableComponent1.doSomething();
    }

    public void doWith2() {
        mockableComponent2.doSomething();
    }

}
