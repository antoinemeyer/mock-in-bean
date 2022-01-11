package com.teketik.test.mockinbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.core.DefaultNamingPolicy;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <p>Tracker in application context gluing beans, proxies and mocks together.
 * <p>Note that this does not inject proxies in beans, it merely keeps references.
 * @author Antoine Meyer
 */
class MockInBeanTracker {

    static final class MockTracker {

        private final Map<Object, Map<Thread, Object>> beanToMockThreadLocal = new IdentityHashMap<>();

        synchronized void track(Object bean, Object mockOrSpy) {
            MapUtils
                .getOrPut(beanToMockThreadLocal, bean, () -> new HashMap<>())
                .put(Thread.currentThread(), mockOrSpy);
        }

        /**
         * @param bean
         * @return true if there are no more mocks being tracked for {@code bean}.
         */
        synchronized boolean untrack(Object bean) {
            return Optional.ofNullable(beanToMockThreadLocal.get(bean))
                .map(threadLocal -> {
                    threadLocal.remove(Thread.currentThread());
                    return threadLocal.isEmpty();
                })
                .orElse(false);
        }

        /**
         * @param bean
         * @return the mock of this {@code bean} related to this thread if more than one mock exist,
         * or that mock if there is only one, or nothing.
         */
        synchronized Optional<Object> getTracked(Object bean) {
            return Optional.ofNullable(beanToMockThreadLocal.get(bean))
                .flatMap(threadToMock -> {
                    if (threadToMock.size() == 1) {
                        /*
                         * If only one mock exists, we return it even if running on a different thread to give
                         * more flexibility to sequential tests.
                         * Parallel tests on the other hand need to run the mock on the same thread than the test
                         * as there is no other way to locate it.
                         */
                        return Optional.of(threadToMock.values().iterator().next());
                    } else {
                        return Optional.ofNullable(threadToMock.get(Thread.currentThread()));
                    }
                });
        }
    }

    static final class ProxyTracker {

        private final Map<Object, Object> proxyToBean = new IdentityHashMap<>();
        private final Map<Object, Object> beanToProxy = new IdentityHashMap<>();

        synchronized Object getByBean(Object bean) {
            return beanToProxy.get(bean);
        }

        synchronized Object getByBeanOrMake(Object bean, Supplier<Object> proxyMaker) {
            return Optional.ofNullable(beanToProxy.get(bean))
                .orElseGet(() -> {
                    final Object proxy = proxyMaker.get();
                    proxyToBean.put(proxy, bean);
                    beanToProxy.put(bean, proxy);
                    return proxy;
                });
        }

        synchronized Object getBeanByProxy(Object proxy) {
            return proxyToBean.get(proxy);
        }
    }

    private static final NamingPolicy ENHANCER_NAMING_POLICY = new DefaultNamingPolicy() {
        @Override
        public String getClassName(String prefix, String source, Object key, Predicate names) {
            return super.getClassName(prefix, source + "MockInBean", key, names);
        }
    };

    static boolean isProxy(Object o) {
        return o.getClass().toString().contains("$$EnhancerMockInBeanByCGLIB$$");
    }

    private final Log logger = LogFactory.getLog(getClass());

    final MockTracker mockTracker = new MockTracker();
    final ProxyTracker proxyTracker = new ProxyTracker();

    public Object setupProxyIfNotExisting(Object beanOrProxy) {
        if (isProxy(beanOrProxy)) {
            return beanOrProxy;
        } else {
            return proxyTracker.getByBeanOrMake(beanOrProxy, () -> {
                logger.debug("Creating proxy of bean " + beanOrProxy);
                return makeProxy(beanOrProxy);
            });
        }
    }

    private Object makeProxy(final Object originalBean) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(originalBean.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object object, Method method, Object[] parameters, MethodProxy methodProxy)
                    throws Throwable {
                final Object target = resolveInvocationTarget(originalBean);
                return method.invoke(target, parameters);
            }

            private Object resolveInvocationTarget(final Object originalBean) {
                final Optional<Object> trackedMock = mockTracker.getTracked(originalBean);
                if (trackedMock.isPresent()) {
                    logger.debug("Resolved mock from thread local for class " + originalBean);
                    return trackedMock.get();
                } else {
                    logger.debug("No mock in thread local, using original " + originalBean);
                    return originalBean;
                }
            }
        });
        enhancer.setNamingPolicy(ENHANCER_NAMING_POLICY);
        return enhancer.create();
    }

}
