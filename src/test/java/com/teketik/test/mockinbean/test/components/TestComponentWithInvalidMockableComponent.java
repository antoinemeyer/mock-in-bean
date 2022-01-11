package com.teketik.test.mockinbean.test.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Profile("altering-component")
@Component
public class TestComponentWithInvalidMockableComponent {

    @Autowired
    private MockableComponent1 mockableComponent1;

    @PostConstruct
    public void alter() {
        mockableComponent1 = new MockableComponent1();
    }

}
