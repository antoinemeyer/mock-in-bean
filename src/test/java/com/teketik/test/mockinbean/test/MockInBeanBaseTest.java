package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.MockableComponent2;
import com.teketik.test.mockinbean.test.components.TestComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


abstract class MockInBeanBaseTest extends BaseTest {

    static class MockRecreationVerifier {
        private volatile MockableComponent1 mockableComponent1firstTest;
        private volatile MockableComponent2 mockableComponent2firstTest;
        private volatile boolean verified;
        private synchronized void verify(MockableComponent1 mockableComponent1, MockableComponent2 mockableComponent2) {
            if (mockableComponent1firstTest == null && mockableComponent2firstTest == null) {
                Assertions.assertNotNull(mockableComponent1);
                Assertions.assertNotNull(mockableComponent2);
                mockableComponent1firstTest = mockableComponent1;
                mockableComponent2firstTest = mockableComponent2;
            } else {
                Assertions.assertNotSame(mockableComponent1, mockableComponent1firstTest);
                Assertions.assertNotSame(mockableComponent2, mockableComponent2firstTest);
                verified = true;
            }
        }
        boolean isVerified() {
            return verified;
        }
    }

    @Autowired
    protected TestComponent1 testComponent1;

    @Autowired
    protected TestComponent2 testComponent2;


    @Test
    public void emptyTestForMockRecreationVerification() {
        verifyMocksChanged();
    }

    abstract MockableComponent1 getMockableComponent1();

    abstract MockableComponent2 getMockableComponent2();

    abstract MockRecreationVerifier getMockRecreationVerifier();

    void verifyMocksChanged() {
        getMockRecreationVerifier().verify(getMockableComponent1(), getMockableComponent2());
    }

}
