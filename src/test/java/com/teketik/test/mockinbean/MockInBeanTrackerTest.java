package com.teketik.test.mockinbean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MockInBeanTrackerTest {

    @Test
    public void testIsMock() {
        final MockInBeanTracker mockInBeanTracker = new MockInBeanTracker();
        final Object object = new Object();
        final Object proxy = mockInBeanTracker.setupProxyIfNotExisting(object);
        Assertions.assertFalse(mockInBeanTracker.isProxy(object));
        Assertions.assertTrue(mockInBeanTracker.isProxy(proxy));
    }
    
}
