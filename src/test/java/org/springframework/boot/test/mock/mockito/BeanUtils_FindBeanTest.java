package org.springframework.boot.test.mock.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@SpringBootTest(classes = BeanUtils_FindBeanTest.FindBeanTestConfig.class)
class BeanUtils_FindBeanTest {

    @Configuration
    static class FindBeanTestConfig {

        static final String STRING = "a";
        static final int INT_1 = 1;
        static final int INT_2 = 2;

        @Bean
        String string() {
            return STRING;
        }

        @Bean
        Integer int1() {
            return INT_1;
        }

        @Bean
        Integer int2() {
            return INT_2;
        }

    }

    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void testFindBean_notFound() {
        try {
            BeanUtils.findBean(Long.class, "does not matter", applicationContext);
            Assertions.fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("No beans of type class java.lang.Long", e.getMessage());
        }
    }

    @Test
    public void testFindBean_oneTypeMatch() {
        Assertions.assertEquals(FindBeanTestConfig.STRING, BeanUtils.findBean(String.class, "different name", applicationContext));
    }

    @Test
    public void testFindBean_found() {
        Assertions.assertEquals(FindBeanTestConfig.STRING, BeanUtils.findBean(String.class, "object", applicationContext));
    }

    @Test
    public void testFindBean_invalidType() {
        // bean name does not matter, we resolve per type first
        Assertions.assertEquals(FindBeanTestConfig.STRING, BeanUtils.findBean(String.class, "object", applicationContext));
    }

    @Test
    public void testFindBean_noName() {
        try {
            BeanUtils.findBean(Integer.class, null, applicationContext);
            Assertions.fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Multiple beans of type class java.lang.Integer. A name must be provided", e.getMessage());
        }
    }

    @Test
    public void testFindBean_multiTypeAndName() {
        Assertions.assertEquals(FindBeanTestConfig.INT_1, BeanUtils.findBean(Integer.class, "int1", applicationContext).intValue());
        Assertions.assertEquals(FindBeanTestConfig.INT_2, BeanUtils.findBean(Integer.class, "int2", applicationContext).intValue());
    }

    @Test
    public void testFindBean_multiTypeAndNameNotFound() {
        try {
            BeanUtils.findBean(Integer.class, "int3", applicationContext);
            Assertions.fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("No beans of type class java.lang.Integer and name int3", e.getMessage());
        }
    }

}
