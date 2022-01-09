package com.teketik.test.mockinbean.test.components;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestComponentWith1 {

    @Resource
    private TestComponentWith2 testComponentWith2;

    public void invokeOther() {
        testComponentWith2.invokeOther();
    }
    
}
