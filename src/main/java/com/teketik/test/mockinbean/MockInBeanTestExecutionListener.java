package com.teketik.test.mockinbean;

import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>{@link TestExecutionListener} handling the creation and injection of {@link Mock}s and {@link Spy}s in the test classes.
 * <p>New {@link Mock}s and {@link Spy}s are created and injected {@link #beforeTestMethod(TestContext)}.
 * <p>Original Spring Beans are re-injected in the related Spring Beans {@link #afterTestClass(TestContext)}.
 * @author Antoine Meyer
 */
class MockInBeanTestExecutionListener extends AbstractTestExecutionListener {

    private static final String ORIGINAL_VALUES_ATTRIBUTE_NAME = "MockInBean.originalValues";

    private static final Map<Class<?>, TestContext> ROOT_TEST_CONTEXT_TRACKER = new ConcurrentHashMap<>(new HashMap<>());

    /*
     * Extracts the mock and spy bean definitions.
     * Visit all the definitions to capture the original values along with the definitions.
     * Ensures no more than one mock/spy definition per field.
     */
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        if (isNestedTestClass(testContext.getTestClass())) {
            return;
        }
        ROOT_TEST_CONTEXT_TRACKER.put(testContext.getTestClass(), testContext);
        final InBeanDefinitionsParser parser = new InBeanDefinitionsParser();
        final Class<?> targetTestClass = resolveTestClass(testContext.getTestClass());
        parser.parse(targetTestClass);
        final Set<Field> visitedFields = new HashSet<>();
        final LinkedList<FieldState> originalValues = new LinkedList<>();
        for (Entry<Definition, List<InBeanDefinition>> definitionToInbeans : parser.getDefinitions().entrySet()) {
            final Definition definition = definitionToInbeans.getKey();
            final Class<?> mockOrSpyType = extractClass(definition);
            Field beanField = null;
            for (InBeanDefinition inBeanDefinition : definitionToInbeans.getValue()) {
                final Object inBean = BeanUtils.findBean(inBeanDefinition.clazz, inBeanDefinition.name, testContext.getApplicationContext());
                beanField = BeanUtils.findField(inBean.getClass(), definition.getName(), mockOrSpyType);
                beanField.setAccessible(true);
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
            final Field testField = ReflectionUtils.findField(targetTestClass, definition.getName(), mockOrSpyType);
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
        final TestContext applicableTestContext = ROOT_TEST_CONTEXT_TRACKER
                .get(resolveTestClass(testContext.getTestClass()));
        final Map<Definition, Object> mockOrSpys = new HashMap<>();
        ((LinkedList<FieldState>) applicableTestContext.getAttribute(ORIGINAL_VALUES_ATTRIBUTE_NAME))
            .forEach(fieldState -> {
                Object mockOrSpy = mockOrSpys.get(fieldState.definition);
                if (mockOrSpy == null) {
                    mockOrSpy = fieldState.definition.create(fieldState.originalValue);
                    mockOrSpys.put(fieldState.definition, mockOrSpy);
                }
                ReflectionUtils.setField(
                    fieldState.field,
                    fieldState.resolveTarget(applicableTestContext),
                    mockOrSpy
                );
            });
        super.beforeTestMethod(testContext);
    }

    /*
     * Iterate over all the definitions and put back the original values in the beans
     */
    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        if (isNestedTestClass(testContext.getTestClass())) {
            return;
        }
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
        ROOT_TEST_CONTEXT_TRACKER.remove(testContext.getTestClass());
        super.afterTestClass(testContext);
    }

    private Class<?> extractClass(Definition definition) {
        Type type = definition.getResolvableType().getType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return (Class<?>) type;
    }

    private Class<?> resolveTestClass(Class<?> candidate) {
        if (isNestedTestClass(candidate)) {
            return resolveTestClass(candidate.getEnclosingClass());
        }
        return candidate;
    }

    private boolean isNestedTestClass(Class<?> candidate) {
        return AnnotationUtils.isAnnotationDeclaredLocally(Nested.class, candidate);
    }

}
