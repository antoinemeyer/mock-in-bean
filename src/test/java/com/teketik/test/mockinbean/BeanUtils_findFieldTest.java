package com.teketik.test.mockinbean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeanUtils_findFieldTest {

    abstract class BaseClazz {
        String baseField;
    }

    class Clazz1 extends BaseClazz {
        String field1;
        String baseField;
    }

    @Test
    public void testFindField_oneMatch() {
        Assertions.assertNull(BeanUtils.findField(BaseClazz.class, null, Integer.class));
        Assertions.assertEquals("baseField", BeanUtils.findField(BaseClazz.class, null, String.class).getName());
        Assertions.assertEquals("baseField", BeanUtils.findField(BaseClazz.class, "baseField", String.class).getName());
        Assertions.assertEquals("baseField", BeanUtils.findField(BaseClazz.class, "not used", String.class).getName());
    }

    @Test
    public void testFindField_multiMatch() {
        try {
            BeanUtils.findField(Clazz1.class, null, String.class).getName();
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Multiple fields of type class java.lang.String in class com.teketik.test.mockinbean.BeanUtils_findFieldTest$Clazz1. Please specify a name.", e.getMessage());
        }
        try {
            BeanUtils.findField(Clazz1.class, "baseField", String.class).getName();
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Multiple fields of type class java.lang.String in class com.teketik.test.mockinbean.BeanUtils_findFieldTest$Clazz1 with name baseField", e.getMessage());
        }
        try {
            BeanUtils.findField(Clazz1.class, "no match", String.class).getName();
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Multiple fields of type class java.lang.String in class com.teketik.test.mockinbean.BeanUtils_findFieldTest$Clazz1 and none with name no match", e.getMessage());
        }
        Assertions.assertEquals("field1", BeanUtils.findField(Clazz1.class, "field1", String.class).getName());
    }

}
