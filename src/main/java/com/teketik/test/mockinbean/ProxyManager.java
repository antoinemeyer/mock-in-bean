package com.teketik.test.mockinbean;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

@Component
public class ProxyManager {

    //mock/spy instance to proxy
    final Map<Object, Object> proxyMap = new IdentityHashMap<>();
 
    final Map<Definition, ThreadLocal<Object>> definitionToThreadLocal = new HashMap<>();

    //TODO since we have a map in the caller method, can this be consolidated???
    
    //TODO syncrhonize determining the mock of threads, not the actual invocation
    
    public synchronized Object getOrCreateProxy(Object unproxiedMockOrSpy, Definition definition) {
        
        
        //TODO maybe use factory to create the proxy?
        
        Object proxy = proxyMap.get(unproxiedMockOrSpy);
        if (proxy == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(unproxiedMockOrSpy.getClass());
            enhancer.setCallback(new MethodInterceptor() {

                @Override
                public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3)
                        throws Throwable {
                    ThreadLocal<Object> obj = definitionToThreadLocal.get(definition);
                    Object originalUnproxiedMaybe = obj.get();
                    return arg1.invoke(originalUnproxiedMaybe, arg2);
                }
                
            });
            proxy = enhancer.create();
            proxyMap.put(unproxiedMockOrSpy, proxy);
        }
        
        
        ThreadLocal<Object> threadLocal = definitionToThreadLocal.get(definition);
        if (threadLocal == null) {
            threadLocal = new ThreadLocal();
            definitionToThreadLocal.put(definition, threadLocal);
        }
        threadLocal.set(unproxiedMockOrSpy);
        return proxy;
    }
    
}
