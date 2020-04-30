package org.ael.plugin.aop.proxy.web;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.ael.http.WebContent;
import org.ael.plugin.enhance.EnhanceInvoke;

import java.lang.reflect.Method;

import static org.ael.plugin.aop.AopPlugin.*;

public class CglibWebInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        WebContent webContent = WEB_CONTENT_THREAD_LOCAL.get();

        Class<?> typeClass = o.getClass();

        ClassLoader classLoader = typeClass.getClassLoader();

        String name = typeClass.getName();
        String className = name.substring(0, name.indexOf('$'));

        Class<?> objectClass = Class.forName(className);

        new EnhanceInvoke().beforeEnhanceExecute(objectClass, method);

        return methodProxy.invokeSuper(o, objects);
    }

    public Class<?> getObjectClass(String name) throws ClassNotFoundException {
        return Class.forName(name.substring(0, name.lastIndexOf('.')));
    }

    public String getName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private boolean isAnnotation(Class<?> cls) {
        return cls.isAnnotationPresent(after)
                || cls.isAnnotationPresent(before)
                || cls.isAnnotationPresent(surround)
                || cls.isAnnotationPresent(exception);
    }
}
