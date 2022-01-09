package com.teketik.test.mockinbean.test;

import org.junit.jupiter.api.Test;

public class Concurrent1Test extends ConcurrentNumberedBaseTest {

    @Test
    public void secondTest() throws InterruptedException {
        test();
    }

}
