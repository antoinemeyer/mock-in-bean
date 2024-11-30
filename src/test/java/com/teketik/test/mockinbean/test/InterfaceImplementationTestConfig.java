package com.teketik.test.mockinbean.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

class InterfaceImplementationTestConfig {

    public interface ProviderService {
        public String provideValue();
    }

    @Service
    public static class ProviderServiceImpl implements ProviderService {
        @Override
        public String provideValue() {
            return "value1";
        }
    }

    @Component
    public static class LoggingService {

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
