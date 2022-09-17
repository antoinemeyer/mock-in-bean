package com.teketik.test.mockinbean.test;

import com.teketik.test.mockinbean.MockInBean;
import com.teketik.test.mockinbean.test.components.MockableComponent1;
import com.teketik.test.mockinbean.test.components.TestComponent1;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/*
 * Ensures that a single mock gets invoked when being run on a separate thread.
 */
//Note that this test cannot run concurrently
@Tag("sequential")
public class SeparateSingleThreadTest extends BaseTest {

    @MockInBean(TestComponent1.class)
    private MockableComponent1 mockableComponent1;

    @Resource
    private TestComponent1 testComponent1;

    @Test
    public void test() throws Exception {
        Executors.newSingleThreadExecutor().submit(() -> {
            //mock is not resolved from thread local but from the single mock that current exists for this bean.
            testComponent1.doWith1();
        }).get(2, TimeUnit.SECONDS);
        Mockito.verify(mockableComponent1).doSomething();
    }

}
