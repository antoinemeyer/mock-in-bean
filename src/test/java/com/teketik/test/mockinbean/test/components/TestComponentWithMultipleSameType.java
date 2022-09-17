package com.teketik.test.mockinbean.test.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestComponentWithMultipleSameType {

    @Autowired
    private MockableComponent2 mockableComponentExtending2;

    @Autowired
    private MockableComponent2 mockableComponent2;

}
