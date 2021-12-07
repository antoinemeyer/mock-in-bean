package com.teketik.test.mockinbean.test.components;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GenericTestComponent {

    @Resource
    private GenericMockableComponent<String> genericMockableComponent;

}
