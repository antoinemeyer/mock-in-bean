package com.teketik.test.mockinbean;

import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <p>{@link TestExecutionListener} handling the creation and injection of {@link Mock}s and {@link Spy}s in the test classes.
 * <p>New {@link Mock}s and {@link Spy}s are created and injected {@link #beforeTestMethod(TestContext)}.
 * <p>Original Spring Beans are re-injected in the related Spring Beans {@link #afterTestClass(TestContext)}.
 * @author Antoine Meyer
 */
class MockInBeanTestExecutionListener extends AbstractTestExecutionListener {

    private static final String ORIGINAL_VALUES_ATTRIBUTE_NAME = "MockInBean.originalValues";

    /*
     * Extracts the mock and spy bean definitions.
     * Visit all the definitions to capture the original values along with the definitions.
     * Ensures no more than one mock/spy definition per field.
     */
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        final InBeanDefinitionsParser parser = new InBeanDefinitionsParser();
        parser.parse(testContext.getTestClass());
        ApplicationContext applicationContext = testContext.getApplicationContext();
        synchronized (applicationContext) {
            if (applicationContext.getBeansOfType(ProxyManager.class).isEmpty()) {
                ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                beanFactory.registerSingleton("proxyManager", new ProxyManager());
            }
        }
        final Set<Field> visitedFields = new HashSet<>();
        final LinkedList<FieldState> originalValues = new LinkedList<>();
        for (Entry<Definition, List<InBeanDefinition>> definitionToInbeans : parser.getDefinitions().entrySet()) {
            final Definition definition = definitionToInbeans.getKey();
            final Class<?> mockOrSpyType = extractClass(definition);
            Field beanField = null;
            for (InBeanDefinition inBeanDefinition : definitionToInbeans.getValue()) {
                beanField = BeanUtils.findField(inBeanDefinition.clazz, definition.getName(), mockOrSpyType);
                beanField.setAccessible(true);
                final Object inBean = BeanUtils.findBean(inBeanDefinition.clazz, inBeanDefinition.name, applicationContext);
                originalValues.add(
                    new BeanFieldState(
                        inBean,
                        beanField,
                        ReflectionUtils.getField(
                            beanField,
                            inBean
                        ),
                        definition
                    )
                );
            }
            Assert.notNull(beanField, "Cannot find any field for definition:" + definitionToInbeans.getKey());
            Assert.isTrue(visitedFields.add(beanField), beanField + " can only be mapped once, as a mock or a spy, not both!");
            final Field testField = ReflectionUtils.findField(testContext.getTestClass(), definition.getName(), mockOrSpyType);
            testField.setAccessible(true);
            originalValues.add(
                new TestFieldState(
                    testField,
                    null,
                    definition
                )
            );
        }
        testContext.setAttribute(ORIGINAL_VALUES_ATTRIBUTE_NAME, originalValues);
        super.beforeTestClass(testContext);
    }

    /*
     * Iterate over all the definitions and create a corresponding mock/spy that is injected in the beans and the test class
     */
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final ProxyManager proxyManager = testContext.getApplicationContext().getBean(ProxyManager.class);
        final Map<Definition, Object> mockOrSpys = new HashMap<>();
        ((LinkedList<FieldState>) testContext.getAttribute(ORIGINAL_VALUES_ATTRIBUTE_NAME))
            .forEach(fieldState -> {
                Object mockOrSpyProxy = mockOrSpys.get(fieldState.definition);
                if (mockOrSpyProxy == null) {
                    
                    
                    Object unproxiedMockOrSpy = fieldState.definition.create(fieldState.originalValue);
                    mockOrSpyProxy = proxyManager.getOrCreateProxy(unproxiedMockOrSpy, fieldState.definition);
                    
                    mockOrSpys.put(fieldState.definition, mockOrSpyProxy);
                }
                ReflectionUtils.setField(
                    fieldState.field,
                    fieldState.resolveTarget(testContext),
                    mockOrSpyProxy
                );
            });
        super.beforeTestMethod(testContext);
    }

    /*
     * Iterate over all the definitions and put back the original values in the beans
     */
    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        ((LinkedList<FieldState>) testContext.getAttribute(ORIGINAL_VALUES_ATTRIBUTE_NAME))
            .forEach(fieldValue -> {
                if (fieldValue.originalValue != null) {
                    ReflectionUtils.setField(
                        fieldValue.field,
                        fieldValue.resolveTarget(testContext),
                        fieldValue.originalValue
                    );
                }
            });
        super.afterTestClass(testContext);
    }

    private Class<?> extractClass(Definition definition) {
        Type type = definition.getResolvableType().getType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return (Class<?>) type;
    }
}
