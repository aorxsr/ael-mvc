package org.ael.plugin.aop.proxy.web;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.ael.http.WebContent;
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

        beforeEnhanceExecute(objectClass, method);

        return methodProxy.invokeSuper(o, objects);
    }

    public Class<?> getObjectClass(String name) throws ClassNotFoundException {
        return Class.forName(name.substring(0, name.lastIndexOf('.')));
    }

    public String getName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private boolean isAnnotation(Class<?> cls) {
        return  cls.isAnnotationPresent(after)
                || cls.isAnnotationPresent(before)
                || cls.isAnnotationPresent(surround)
                || cls.isAnnotationPresent(exception);
    }


    private void beforeEnhanceExecute(Class<?> typeClass, Method method) throws Exception {
        String beforeMethodName = null;
        if (method.isAnnotationPresent(before)) {
            beforeMethodName = method.getAnnotation(before).enhanceMethodName();
        } else {
            if (typeClass.isAnnotationPresent(before)) {
                beforeMethodName = typeClass.getAnnotation(before).enhanceMethodName();
            }
        }
        if ("".equals(beforeMethodName)) {
            throw new RuntimeException("没有设置增强方法...");
        } else {
            // 去增强
            invoke(beforeMethodName);
        }
    }

    public void invoke(String invokeMethodName) throws Exception {
        // 类的名称,用Class.forName进行反射获取
        String objPath = invokeMethodName.substring(0, invokeMethodName.lastIndexOf('.'));
        // 反射的方法名称
        String methodName = invokeMethodName.substring(invokeMethodName.lastIndexOf('.') + 1);
        Class<?> objClass = Class.forName(objPath);
        Object object = WebContent.ael.getIocPlugin().buildObject(objClass, objClass.newInstance());
        // 反射
        Method method = objClass.getMethod(methodName);
        method.invoke(object, new Object[]{});
    }

}
