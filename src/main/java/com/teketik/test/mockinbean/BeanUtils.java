package com.teketik.test.mockinbean;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public abstract class BeanUtils {

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
    public static <T> T findBean(Class<T> type, @Nullable String name, ApplicationContext applicationContext) {
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
    public static Field findField(Class<?> clazz, @Nullable String name, Class<?> type) {
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
        }, field -> field.getType().equals(type));
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

    /**
     * Adds a bean named {@code beanName} to {@code applicationContext} as supplied by {@code supplier} if it does not yet exist.
     * @param <T>
     * @param beanName
     * @param supplier
     * @param applicationContext
     * @return the bean (from context, or as supplied).
     */
    public static <T> T addToContextIfMissing(String beanName, Supplier<T> supplier, ApplicationContext applicationContext) {
        final T object;
        synchronized (applicationContext) {
            if (applicationContext.containsBean(beanName)) {
                object = (T) applicationContext.getBean(beanName);
            } else {
                ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                object = supplier.get();
                beanFactory.registerSingleton(beanName, object);
            }
        }
        return object;
    }

}
