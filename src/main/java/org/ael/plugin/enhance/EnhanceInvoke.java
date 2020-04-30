package org.ael.plugin.enhance;

import org.ael.http.WebContent;

import java.lang.reflect.Method;

import static org.ael.plugin.aop.AopPlugin.before;

public class EnhanceInvoke {
    public void beforeEnhanceExecute(Class<?> typeClass, Method method) throws Exception {
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

    private void invoke(String invokeMethodName) throws Exception {
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
