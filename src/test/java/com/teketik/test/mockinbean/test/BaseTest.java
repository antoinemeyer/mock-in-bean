package com.teketik.test.mockinbean.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

@Execution(ExecutionMode.CONCURRENT)
/*
 * Note that by default, tests will not run concurrently.
 * They would only run concurrently through maven
 */
@TestExecutionListeners(
        value = { AssertingCleanTestExecutionListener.class },
        mergeMode = MergeMode.MERGE_WITH_DEFAULTS
)
@SpringBootTest
abstract class BaseTest {

    @AfterEach
    public void validateMocks() {
        Mockito.validateMockitoUsage();
    }

}
