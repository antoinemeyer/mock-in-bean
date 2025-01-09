package com.teketik.test.mockinbean;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

abstract class BeanUtils {

    private BeanUtils() {
    }

    /**
     * <p>Attempt to find the spring bean corresponding to {@code type} if only one exists ({@code name} not used).
     * If multiple exist for that {@code type}, return the one with the corresponding {@code name}.
     * @param <T>
     * @param type
     * @param name <i>may be null</i>
     * @param applicationContext
     * @return the bean, if found
     */
    static <T> T findBean(Class<T> type, @Nullable String name, ApplicationContext applicationContext) {
        final Map<String, T> beansOfType = applicationContext.getBeansOfType(type);
        Assert.isTrue(!beansOfType.isEmpty(), () -> "No beans of type " + type);
        final T beanOrProxy;
        if (beansOfType.size() == 1) {
            beanOrProxy = beansOfType
                .values()
                .iterator()
                .next();
        } else {
            Assert.notNull(name, () -> "Multiple beans of type " + type + ". A name must be provided");
            beanOrProxy = beansOfType
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase(name))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No beans of type " + type + " and name " + name));
        }
        return AopUtils.isAopProxy(beanOrProxy)
            ? (T) AopProxyUtils.getSingletonTarget(beanOrProxy)
            : beanOrProxy;
    }

    /**
     * <p>Attempt to find a {@link Field field} on the supplied {@link Class class} with the
     * supplied {@code name} and {@code type} OR just the {@code type} if none with this {@code name} exists.
     * <p>Searches all superclasses up to {@link Object}.
     * @param clazz
     * @param name
     * @param type
     * @return the field, if found.
     */
    static Field findField(Class<?> clazz, @Nullable String name, Class<?> type) {
        final Object[] results = new Object[2]; //name+type as 0, type only as [1]
        ReflectionUtils.doWithFields(clazz, field -> {
            if (name != null && field.getName().equalsIgnoreCase(name)) {
                if (results[0] == null) {
                    results[0] = field;
                } else {
                    results[0] = Boolean.FALSE; //multiple matching fields
                }
            }
            if (results[1] == null) {
                results[1] = field;
            } else {
                results[1] = Boolean.FALSE; //multiple matching fields
            }
        }, field -> field.getType().isAssignableFrom(type));
        if (results[0] != null) {
            Assert.isTrue(
                !(results[0] instanceof Boolean),
                () -> "Multiple fields of type " + type + " in " + clazz + " with name " + name
            );
            return (Field) results[0];
        }
        if (results[1] != null) {
            Assert.isTrue(
                !(results[1] instanceof Boolean),
                () -> "Multiple fields of type " + type + " in " + clazz
                    + (
                        name != null
                        ? " and none with name " + name
                        : ". Please specify a name."
                    )
            );
            return (Field) results[1];
        }
        return null;
    }

    static @Nullable TargetSource getProxyTarget(Object candidate) {
        try {
            while (AopUtils.isCglibProxy(candidate) && candidate instanceof Advised) {
                Advised advised = (Advised) candidate;
                TargetSource targetSource = advised.getTargetSource();

                if (targetSource.isStatic()) {
                    Object target = targetSource.getTarget();

                    if (target == null || !AopUtils.isAopProxy(target)) {
                        return targetSource;
                    }
                    candidate = target;
                } else {
                    return null;
                }
            }
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to unwrap proxied object", ex);
        }
        return null;
    }

}
