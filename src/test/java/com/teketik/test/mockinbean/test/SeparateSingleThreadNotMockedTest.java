package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/*
 * Ensures that original bean gets invoked when no mock exists
 */
//Note that this test cannot run concurrently
@Tag("sequential")
public class SeparateSingleThreadNotMockedTest extends BaseTest {

    @MockInBean(TestComponent1.class)
    private MockableComponent1 mockableComponent1;

    @Resource
    private TestComponent1 testComponent1;

    @Test
    public void test() throws Exception {
        try {
            Executors.newSingleThreadExecutor().submit(() -> {
                //invoke method with mockableComponent2 that is not mocked. So invokes original bean.
                testComponent1.doWith2();
            }).get(2, TimeUnit.SECONDS);
            Assertions.fail();
        } catch (ExecutionException e) {
            Assertions.assertTrue(e.getCause() instanceof UnsupportedOperationException);
        }
    }

}
