package com.teketik.mockinbean.components;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class TestComponentBase {

    @Autowired
    private MockableComponent1 mockableComponent1;

    @Autowired
    private MockableComponent2 mockableComponent2;

}
