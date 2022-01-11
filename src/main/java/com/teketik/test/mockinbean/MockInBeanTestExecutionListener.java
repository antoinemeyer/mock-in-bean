package com.teketik.test.mockinbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;

class MockInBeanTestExecutionListener extends AbstractTestExecutionListener {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final MockInBeanTestContextManager.Context context = MockInBeanTestContextManager.prepareOnce(testContext);
        context
            .getTestProcessingPayloads()
            .forEach(testProcessingPayload -> {
                final Object mockOrSpy = testProcessingPayload.definition.create(testProcessingPayload.originalBean);
                logger.debug("Injecting mock " + mockOrSpy + " in test field " + testProcessingPayload.testField);
                ReflectionUtils.setField(
                    testProcessingPayload.testField,
                    testContext.getTestInstance(),
                    mockOrSpy
                );
                context.wireMock(testProcessingPayload, mockOrSpy);
            });
        super.beforeTestMethod(testContext);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        final MockInBeanTestContextManager.Context context = MockInBeanTestContextManager.prepareOnce(testContext);
        Optional
            .ofNullable(context.getTestProcessingPayloads())
            .ifPresent(testProcessingPayloads -> {
                for (TestProcessingPayload testProcessingPayload: testProcessingPayloads) {
                    context.unwireMock(testProcessingPayload);
                }
            });
        super.afterTestMethod(testContext);
    }

    @Override
    public int getOrder() {
        return MockInBeanTestExecutionListenerConfig.ORDER;
    }
}
