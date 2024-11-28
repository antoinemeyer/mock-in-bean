package com.teketik.test.mockinbean.test;


import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.InterfaceImplTest.Config.LoggingService;
import com.teketik.test.mockinbean.test.InterfaceImplTest.Config.ProviderServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class InterfaceImplTest extends BaseTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {

        public interface ProviderService {
            public String provideValue();
        }

        @Service
        public class ProviderServiceImpl implements ProviderService {
            @Override
            public String provideValue() {
                return "value1";
            }
        }

        @Component
        public class LoggingService {

            @Autowired
            private ProviderService providerService;

            @Autowired
            private ProviderServiceImpl providerServiceImpl;

            public String logCurrentValue() {
                return providerService.provideValue();
            }

            public String logCurrentValueWithImpl() {
                return providerServiceImpl.provideValue();
            }

        }
    }

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
