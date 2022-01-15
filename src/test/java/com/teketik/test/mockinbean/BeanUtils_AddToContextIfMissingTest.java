package com.teketik.test.mockinbean;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

@SpringBootTest
@SpringBootConfiguration
class BeanUtils_AddToContextIfMissingTest {

    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void test() {
        final Object object = new Object();
        final Object addedToContext = BeanUtils.addToContextIfMissing("beanName", () -> object, applicationContext);
        Assertions.assertSame(object, addedToContext);
        Assertions.assertSame(object, applicationContext.getBean("beanName"));
        final Object alreadyInContext = BeanUtils.addToContextIfMissing("beanName", () -> {
            throw new RuntimeException();
        }, applicationContext);
        Assertions.assertSame(object, alreadyInContext);
    }

}
