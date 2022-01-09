package com.teketik.test.mockinbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;

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

        private final Map<String, Map<Thread, Object>> beanNameToMockThreadLocal = new HashMap<>();

        synchronized void track(String beanName, Object mockOrSpy) {
            MapUtils
                .getOrPut(beanNameToMockThreadLocal, beanName, () -> new HashMap<>())
                .put(Thread.currentThread(), mockOrSpy);
        }

        /**
         * @param beanName
         * @return true if there are no more mocks being tracked for {@code beanName}.
         */
        synchronized boolean untrack(String beanName) {
            return Optional.ofNullable(beanNameToMockThreadLocal.get(beanName))
                .map(threadLocal -> {
                    threadLocal.remove(Thread.currentThread());
                    return threadLocal.isEmpty();
                })
                .orElse(false);
        }

        /**
         * @param beanName
         * @return the mock related to this thread if more than one mock exist,
         * or that mock if there is only one, or nothing.
         */
        synchronized Optional<Object> getTracked(String beanName) {
            return Optional.ofNullable(beanNameToMockThreadLocal.get(beanName))
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

        private final Map<Object, String> proxyToBeanName = new IdentityHashMap<>();

        private final Map<String, Object> beanNameToProxy = new HashMap<>();

        synchronized Object getByName(String beanName) {
            return beanNameToProxy.get(beanName);
        }

        synchronized Object getByNameOrMake(String beanName, Supplier<Object> proxyMaker) {
            return Optional.ofNullable(beanNameToProxy.get(beanName))
                .orElseGet(() -> {
                    final Object proxy = proxyMaker.get();
                    proxyToBeanName.put(proxy, beanName);
                    beanNameToProxy.put(beanName, proxy);
                    return proxy;
                });
        }

        synchronized String getNameByProxy(Object proxy) {
            return proxyToBeanName.get(proxy);
        }
    }

    private final Log logger = LogFactory.getLog(getClass());

    final MockTracker mockTracker = new MockTracker();

    final ProxyTracker proxyTracker = new ProxyTracker();

    public NamedObject setupProxyIfNotExisting(Object beanOrProxy, ApplicationContext applicationContext) {
        return BeanUtils.findBeanName(beanOrProxy, applicationContext)
            .map(beanName -> {
                //not a proxy yet
                final Object proxy = proxyTracker.getByNameOrMake(beanName, () -> {
                    logger.debug("Creating proxy of bean " + beanOrProxy + " with bean name " + beanName + " for " + applicationContext);
                    return makeProxy(beanOrProxy, beanName);
                });
                return new NamedObject(beanName, proxy);
            })
            .orElseGet(() -> {
                //already a proxy
                return new NamedObject(proxyTracker.getNameByProxy(beanOrProxy), beanOrProxy);
            });
    }

    private Object makeProxy(final Object originalBean, String beanName) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(originalBean.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object object, Method method, Object[] parameters, MethodProxy methodProxy)
                    throws Throwable {
                final Object target = resolveInvocationTarget(originalBean, beanName);
                return method.invoke(target, parameters);
            }

            private Object resolveInvocationTarget(final Object originalBean, final String beanName) {
                final Optional<Object> trackedMock = mockTracker.getTracked(beanName);
                if (trackedMock.isPresent()) {
                    logger.debug("Resolved mock from thread local for class " + originalBean);
                    return trackedMock.get();
                } else {
                    logger.debug("No mock in thread local, using original " + originalBean);
                    return originalBean;
                }
            }
        });
        return enhancer.create();
    }

}
