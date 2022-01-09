package com.teketik.test.mockinbean.test.components;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestComponentWith2 {

    @Resource
    private TestComponentWith1 testComponentWith1;
 
    public void invokeOther() {
        testComponentWith1.invokeOther();
    }

}
