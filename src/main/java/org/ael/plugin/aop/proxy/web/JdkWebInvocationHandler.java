package org.ael.plugin.aop.proxy.web;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkWebInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Jdk");
        return method.invoke(proxy, args);
    }

}
