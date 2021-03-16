package com.teketik.mockinbean;

import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.Spy;

abstract class TestUtils {

    private TestUtils() {}

    /**
     * @param object
     * @return true if {@code object} is a {@link Mock} (and not a {@link Spy}!!)
     */
    public static boolean isMock(Object object) {
        final MockingDetails mockingDetails = Mockito.mockingDetails(object);
        return mockingDetails.isMock() && !mockingDetails.isSpy();
    }

    /**
     * @param object
     * @return true if {@code object} is a {@link Spy}
     */
    public static boolean isSpy(Object object) {
        return Mockito.mockingDetails(object).isSpy();
    }

    /**
     * @param object
     * @return true if {@code object} is a {@link Mock} or a {@link Spy}.
     */
    public static boolean isMockOrSpy(Object object) {
        return Mockito.mockingDetails(object).isMock();
    }


}
