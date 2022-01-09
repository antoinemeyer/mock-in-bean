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
        private final ApplicationContext applicationContext;

        private Context(Collection<TestProcessingPayload> testProcessingPayloads, MockInBeanTracker tracker, ApplicationContext applicationContext) {
            super();
            this.testProcessingPayloads = testProcessingPayloads;
            this.tracker = tracker;
            this.applicationContext = applicationContext;
        }

        Collection<TestProcessingPayload> getTestProcessingPayloads() {
            return testProcessingPayloads;
        }

        void wireMock(TestProcessingPayload testProcessingPayload, Object mockOrSpy) {
            synchronized (applicationContext) {
                inject(testProcessingPayload.beansFieldsToInject, tracker.proxyTracker.getByName(testProcessingPayload.originalBean.name));
                tracker.mockTracker.track(testProcessingPayload.originalBean.name, mockOrSpy);
                logger.debug("Tracking mock " + mockOrSpy + " for bean name " + testProcessingPayload.originalBean.name);
            }
        }

        void unwireMock(TestProcessingPayload testProcessingPayload) {
            synchronized (applicationContext) {
                logger.debug("Untracking mock for bean name " + testProcessingPayload.originalBean.name);
                if (tracker.mockTracker.untrack(testProcessingPayload.originalBean.name)) {
                    logger.debug("No more mocks for " + testProcessingPayload.originalBean.name + ". Rollbacking proxy");
                    inject(testProcessingPayload.beansFieldsToInject, testProcessingPayload.originalBean.object);
                }
            }
        }

        private static void inject(final LinkedMultiValueMap<Field, Object> beansFieldsToInject, Object toInject) {
            for (Entry<Field, List<Object>> fieldToBeans: beansFieldsToInject.entrySet()) {
                for (Object bean : fieldToBeans.getValue()) {
                    ReflectionUtils.setField(
                        fieldToBeans.getKey(),
                        bean,
                        toInject
                    );
                }
            }
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
                synchronized (applicationContext) {
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
            }

            private TestProcessingPayload processDefinitions(Definition definition, List<InBeanDefinition> inBeanDefinitions) {
                final Class<?> mockOrSpyType = extractClass(definition);
                final LinkedMultiValueMap<Field, Object> injectedBeanFields = new LinkedMultiValueMap<>();

                String beanNameToProxy = null;
                NamedObject proxy = null;

                for (InBeanDefinition inBeanDefinition : inBeanDefinitions) {
                    final Field inBeanDefinitionField = BeanUtils.findField(inBeanDefinition.clazz, definition.getName(), mockOrSpyType);
                    Assert.notNull(inBeanDefinitionField, () -> "Cannot find bean to mock " + mockOrSpyType + " in " + inBeanDefinition.clazz);
                    inBeanDefinitionField.setAccessible(true);
                    final Object inBean = BeanUtils.findBean(inBeanDefinition.clazz, inBeanDefinition.name, applicationContext);
                    final Object originalFieldValueInBean = ReflectionUtils.getField(
                        inBeanDefinitionField,
                        inBean
                    );

                    proxy = tracker.setupProxyIfNotExisting(originalFieldValueInBean, applicationContext);
                    Assert.isTrue(
                        beanNameToProxy == null || beanNameToProxy.equals(proxy.name),
                        () -> "Resolved invalid target beans for definition " + definition
                    );
                    beanNameToProxy = proxy.name;
                    injectedBeanFields.add(inBeanDefinitionField, inBean);

                    Assert.isTrue(
                        MapUtils.getOrPut(alreadyProxiedFields, inBean, () -> new HashSet<>())
                            .add(inBeanDefinitionField),
                        () -> inBeanDefinitionField + " can only be mapped once, as a mock or a spy, not both!"
                    );
                }

                Assert.notNull(beanNameToProxy, () -> "Invalid target bean name for definition " + definition);
                Assert.notNull(proxy, () -> "Invalid proxy for definition " + definition);

                final Field testField = ReflectionUtils.findField(testContext.getTestClass(), definition.getName(), mockOrSpyType);
                testField.setAccessible(true);

                return new TestProcessingPayload(
                    definition,
                    injectedBeanFields,
                    testField,
                    new NamedObject(beanNameToProxy, applicationContext.getBean(beanNameToProxy))
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
                    tracker,
                    applicationContext
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
