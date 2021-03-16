package com.teketik.mockinbean.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestComponentWithMultipleSameType {

    @Autowired
    private MockableComponent1 mockableComponent1a;

    @Autowired
    private MockableComponent1 mockableComponent1b;

}
