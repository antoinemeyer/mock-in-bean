package com.teketik.mockinbean;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

@TestExecutionListeners(
        value = { AssertingCleanTestExecutionListener.class },
        mergeMode = MergeMode.MERGE_WITH_DEFAULTS
)
@SpringBootTest
abstract class BaseTest {

}
