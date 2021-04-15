package com.teketik.test.mockinbean;

import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Similar to {@link org.springframework.boot.test.mock.mockito.DefinitionsParser} but handles {@link MockInBean} and {@link SpyInBean}.
 * <p>Every mock/spy {@link Definition} maps to one or more {@link InBeanDefinition}.
 * @see DefinitionsParser
 */
class InBeanDefinitionsParser {

    private final Map<Definition, List<InBeanDefinition>> definitions = new HashMap<Definition, List<InBeanDefinition>>();

    void parse(Class<?> source) {
        ReflectionUtils.doWithFields(source, (field) -> parseField(field, source));
    }

    private void parseField(Field element, Class<?> source) {
        final MergedAnnotations annotations = MergedAnnotations.from(element, SearchStrategy.SUPERCLASS);
        for (MockInBean annotation: AnnotationUtils.getRepeatableAnnotations(element, MockInBean.class, MockInBeans.class)) {
            parseMockInBeanAnnotation(annotation, element, source);
        }
        for (SpyInBean annotation: AnnotationUtils.getRepeatableAnnotations(element, SpyInBean.class, SpyInBeans.class)) {
            parseSpyInBeanAnnotation(annotation, element, source);
        }
    }

    private void parseMockInBeanAnnotation(MockInBean annotation, Field field, Class<?> source) {
        final Set<ResolvableType> typesToMock = getOrDeduceTypes(field, source);
        Assert.state(!typesToMock.isEmpty(), () -> "Unable to deduce type to mock from " + field);
        for (ResolvableType typeToMock : typesToMock) {
            final Definition definition = new MockDefinition(
                field.getName(),
                typeToMock
            );
            final InBeanDefinition inBeanDefinition = new InBeanDefinition(
                annotation.value(),
                StringUtils.isEmpty(annotation.name()) ? null : annotation.name()
            );
            addDefinition(definition, inBeanDefinition);
        }
    }

    private void parseSpyInBeanAnnotation(SpyInBean annotation, Field field, Class<?> source) {
        final Set<ResolvableType> typesToSpy = getOrDeduceTypes(field, source);
        Assert.state(!typesToSpy.isEmpty(), () -> "Unable to deduce type to spy from " + field);
        for (ResolvableType typeToSpy : typesToSpy) {
            final Definition definition = new SpyDefinition(
                field.getName(),
                typeToSpy
            );
            final InBeanDefinition inBeanDefinition = new InBeanDefinition(
                annotation.value(),
                StringUtils.isEmpty(annotation.name()) ? null : annotation.name()
            );
            addDefinition(definition, inBeanDefinition);
        }
    }

    private void addDefinition(Definition definition, InBeanDefinition inBeanDefinition) {
        List<InBeanDefinition> inBeanBaseDefinitions = definitions.get(definition);
        if (inBeanBaseDefinitions == null) {
            inBeanBaseDefinitions = new LinkedList<InBeanDefinition>();
            definitions.put(definition, inBeanBaseDefinitions);
        }
        inBeanBaseDefinitions.add(inBeanDefinition);
    }

    private Set<ResolvableType> getOrDeduceTypes(AnnotatedElement element, Class<?> source) {
        Set<ResolvableType> types = new LinkedHashSet<>();
        if (types.isEmpty() && element instanceof Field) {
            Field field = (Field) element;
            types.add(
                (field.getGenericType() instanceof TypeVariable)
                    ? ResolvableType.forField(field, source)
                    : ResolvableType.forField(field)
            );
        }
        return types;
    }

    public Map<Definition, List<InBeanDefinition>> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }

}
