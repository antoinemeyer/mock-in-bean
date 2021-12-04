package com.teketik.test.mockinbean.test.components;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InterceptedComponent {

    @Resource
    private MockableComponent1 mockableComponent1;

    public void process() {
        mockableComponent1.doSomething();
    }

}
