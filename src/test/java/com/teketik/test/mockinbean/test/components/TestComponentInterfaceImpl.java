package com.teketik.test.mockinbean.test.components;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestComponentInterfaceImpl implements TestComponentInterface {

    @Resource
    private TestComponent1 testComponent1;

}
