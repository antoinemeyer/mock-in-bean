package com.teketik.test.mockinbean.test;

import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

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
