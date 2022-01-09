package com.teketik.test.mockinbean;

import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.Field;

class TestProcessingPayload {

    final Definition definition;

    final LinkedMultiValueMap<Field, Object> beansFieldsToInject;

    final Field testField;

    final NamedObject originalBean;

    public TestProcessingPayload(Definition definition, LinkedMultiValueMap<Field, Object> beansFieldsToInject, Field testField, NamedObject originalBean) {
        super();
        this.definition = definition;
        this.beansFieldsToInject = beansFieldsToInject;
        this.testField = testField;
        this.originalBean = originalBean;
    }

    @Override
    public String toString() {
        return "TestProcessingPayload [definition=" + definition + ", beansFieldsToInject=" + beansFieldsToInject + ", testField=" + testField + ", originalBean="
                + originalBean + "]";
    }

}
