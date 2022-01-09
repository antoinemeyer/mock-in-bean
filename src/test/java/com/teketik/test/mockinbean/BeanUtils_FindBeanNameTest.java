package com.teketik.test.mockinbean;


import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@SpringBootTest(classes = BeanUtils_FindBeanNameTest.Config.class)
class BeanUtils_FindBeanNameTest {

    @Configuration
    static class Config {

        @Bean(name = "bean1")
        String string() {
            return "";
        }

        @Bean
        String bean2() {
            return "";
        }

    }

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private String bean1;

    @Resource
    private String bean2;

    @Test
    public void test() {
         Assert.assertEquals("bean1", BeanUtils.findBeanName(bean1, applicationContext).get());
         Assert.assertFalse(BeanUtils.findBeanName("bean2", applicationContext).isPresent());
         Assert.assertFalse(BeanUtils.findBeanName(new Object(), applicationContext).isPresent());
    }

}
