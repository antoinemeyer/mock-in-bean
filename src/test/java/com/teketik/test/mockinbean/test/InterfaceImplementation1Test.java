package com.teketik.test.mockinbean.test;


import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.InterfaceImplementationTestConfig.LoggingService;
import com.teketik.test.mockinbean.test.InterfaceImplementationTestConfig.ProviderServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(InterfaceImplementationTestConfig.class)
public class InterfaceImplementation1Test extends BaseTest {

    @Autowired
    protected LoggingService loggingService;

    @MockInBean(LoggingService.class)
    private ProviderServiceImpl providerService;

    @MockInBean(LoggingService.class)
    private ProviderServiceImpl providerServiceImpl;

    @Test
    public void test() {
        Mockito.when(providerService.provideValue()).thenReturn("mocked value");
        Mockito.when(providerServiceImpl.provideValue()).thenReturn("mocked value 2");

        Assertions.assertEquals("mocked value", loggingService.logCurrentValue());
        Assertions.assertEquals("mocked value 2", loggingService.logCurrentValueWithImpl());
    }

}
