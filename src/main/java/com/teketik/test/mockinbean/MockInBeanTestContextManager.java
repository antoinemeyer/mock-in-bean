package com.teketik.test.mockinbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Responsible for wiring/unwiring mocks in the proxies which are injected in the spring beans.
 * @author Antoine Meyer
 */
class MockInBeanTestContextManager {

    private static final String MOCK_IN_BEAN_TEST_CONTEXT = "MockInBeanTestContext";

    static class Context {

        private final Log logger = LogFactory.getLog(getClass());

        private final Collection<TestProcessingPayload> testProcessingPayloads;
        private final MockInBeanTracker tracker;

        private Context(Collection<TestProcessingPayload> testProcessingPayloads, MockInBeanTracker tracker) {
            super();
            this.testProcessingPayloads = testProcessingPayloads;
            this.tracker = tracker;
        }

        Collection<TestProcessingPayload> getTestProcessingPayloads() {
            return testProcessingPayloads;
        }

        void wireMock(TestProcessingPayload testProcessingPayload, Object mockOrSpy) {
            synchronized (testProcessingPayload.originalBean) {
                inject(testProcessingPayload.beansFieldsToInject, tracker.proxyTracker.getByBean(testProcessingPayload.originalBean));
                tracker.mockTracker.track(testProcessingPayload.originalBean, mockOrSpy);
                logger.debug("Tracking mock " + mockOrSpy + " for bean " + testProcessingPayload.originalBean);
            }
        }

        void unwireMock(TestProcessingPayload testProcessingPayload) {
            synchronized (testProcessingPayload.originalBean) {
                logger.debug("Untracking mock for bean " + testProcessingPayload.originalBean);
                if (tracker.mockTracker.untrack(testProcessingPayload.originalBean)) {
                    logger.debug("No more mocks for " + testProcessingPayload.originalBean + ". Rollbacking proxy");
                    inject(testProcessingPayload.beansFieldsToInject, testProcessingPayload.originalBean);
                }
            }
        }

        private void inject(final LinkedMultiValueMap<Field, Object> beansFieldsToInject, Object toInject) {
            for (Entry<Field, List<Object>> fieldToBeans: beansFieldsToInject.entrySet()) {
                for (Object bean : fieldToBeans.getValue()) {
                    //inject in bean
                    inject(fieldToBeans.getKey(), bean, toInject);
                    //if the target bean has been spied on, need to push into this spy as well (to allow mock in spies)
                    tracker.mockTracker.getTracked(bean)
                        .ifPresent(mockOrSpy ->inject(fieldToBeans.getKey(), mockOrSpy, toInject));
                }
            }
        }

        private void inject(Field field, Object inObject, Object toInject) {
            if (logger.isDebugEnabled()) {
                logger.debug("Setting " + toInject.getClass() + " in " + field + " of " + inObject.getClass());
            }
            ReflectionUtils.setField(
                field,
                inObject,
                toInject
            );
        }

        private static class Builder {

            private final Log logger = LogFactory.getLog(getClass());

            private final TestContext testContext;
            private final ApplicationContext applicationContext;

            private final Map<Object, Set<Field>> alreadyProxiedFields = new IdentityHashMap<>();

            private final LinkedList<TestProcessingPayload> testProcessingPayloads = new LinkedList<>();
            private final MockInBeanTracker tracker;

            private Builder(TestContext testContext) {
                this.testContext = testContext;
                final InBeanDefinitionsParser parser = new InBeanDefinitionsParser();
                parser.parse(testContext.getTestClass());
                this.applicationContext = testContext.getApplicationContext();
                this.tracker = BeanUtils.addToContextIfMissing("mockInBeanTracker", () -> new MockInBeanTracker(), applicationContext);
                for (Entry<Definition, List<InBeanDefinition>> definitionToInbeans : parser.getDefinitions().entrySet()) {
                    final TestProcessingPayload testProcessingPayload = processDefinitions(
                        definitionToInbeans.getKey(),
                        definitionToInbeans.getValue()
                    );
                    logger.debug("Resolved " + testProcessingPayload);
                    this.testProcessingPayloads.add(testProcessingPayload);
                }
            }

            private TestProcessingPayload processDefinitions(Definition definition, List<InBeanDefinition> inBeanDefinitions) {
                final Class<?> mockOrSpyType = extractClass(definition);
                final LinkedMultiValueMap<Field, Object> beanFieldsToInject = new LinkedMultiValueMap<>();

                Object proxy = null;
                for (InBeanDefinition inBeanDefinition: inBeanDefinitions) {
                    final Object inBean = BeanUtils.findBean(inBeanDefinition.clazz, inBeanDefinition.name, applicationContext);
                    final Field inBeanDefinitionField = BeanUtils.findField(inBean.getClass(), definition.getName(), mockOrSpyType);
                    Assert.notNull(inBeanDefinitionField, () -> "Cannot find bean to mock " + mockOrSpyType + " in " + inBeanDefinition.clazz);
                    inBeanDefinitionField.setAccessible(true);
                    final Object originalFieldValueInBean = ReflectionUtils.getField(
                        inBeanDefinitionField,
                        inBean
                    );

                    //ensure all those field definitions target the same bean (and end up being the same proxy)
                    {
                        final Object proxyForFieldValue = tracker.setupProxyIfNotExisting(originalFieldValueInBean);
                        Assert.isTrue(
                            proxy == null || proxy == proxyForFieldValue,
                            () -> "Resolved invalid target beans for definition " + definition + " in " + inBeanDefinition
                        );
                        proxy = proxyForFieldValue;
                    }

                    beanFieldsToInject.add(inBeanDefinitionField, inBean);

                    Assert.isTrue(
                        MapUtils.getOrPut(alreadyProxiedFields, inBean, () -> new HashSet<>())
                            .add(inBeanDefinitionField),
                        () -> inBeanDefinitionField + " can only be mapped once, as a mock or a spy, not both!"
                    );
                }

                final Field testField = ReflectionUtils.findField(testContext.getTestClass(), definition.getName(), mockOrSpyType);
                testField.setAccessible(true);

                return new TestProcessingPayload(
                    definition,
                    beanFieldsToInject,
                    testField,
                    tracker.proxyTracker.getBeanByProxy(proxy)
                );
            }

            private Class<?> extractClass(Definition definition) {
                Type type = definition.getResolvableType().getType();
                if (type instanceof ParameterizedType) {
                    type = ((ParameterizedType) type).getRawType();
                }
                return (Class<?>) type;
            }

            public Context build() {
                return new Context(
                    Collections.unmodifiableCollection(testProcessingPayloads),
                    tracker
                );
            }
        }
    }

    private MockInBeanTestContextManager() {}

    public static Context prepareOnce(TestContext testContext) {
        synchronized (testContext) {
            Object attribute = testContext.getAttribute(MOCK_IN_BEAN_TEST_CONTEXT);
            if (attribute == null) {
                attribute = new Context.Builder(testContext).build();
                testContext.setAttribute(MOCK_IN_BEAN_TEST_CONTEXT, attribute);
            }
            return (Context) attribute;
        }
    }

}
