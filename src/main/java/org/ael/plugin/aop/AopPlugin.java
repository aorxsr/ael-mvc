/* Copyright (c) 2019, aorxsr (aorxsr@163.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ael.plugin.aop;

import net.sf.cglib.proxy.Enhancer;
import org.ael.Ael;
import org.ael.http.WebContent;
import org.ael.ioc.core.BeanInfo;
import org.ael.ioc.core.annotation.Injection;
import org.ael.plugin.aop.annotation.AfterEnhance;
import org.ael.plugin.aop.annotation.BeforeEnhance;
import org.ael.plugin.aop.annotation.ExceptionEnhance;
import org.ael.plugin.aop.annotation.SurroundEnhance;
import org.ael.plugin.aop.proxy.web.CglibWebIntercepter;
import org.ael.plugin.aop.proxy.web.JdkWebInvocationHandler;
import org.ael.plugin.ioc.IocPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author aorxsr
 * @Data 2020/2/19
 */
public class AopPlugin {

    public static final ThreadLocal<WebContent> WEB_CONTENT_THREAD_LOCAL = new ThreadLocal<>();

    public static final Class<AfterEnhance> after = AfterEnhance.class;
    public static final Class<BeforeEnhance> before = BeforeEnhance.class;
    public static final Class<SurroundEnhance> surround = SurroundEnhance.class;
    public static final Class<ExceptionEnhance> exception = ExceptionEnhance.class;

    private Ael ael;

    public AopPlugin() {
    }

    public void init(Ael ael) {
        this.ael = ael;
        IocPlugin iocPlugin = ael.getIocPlugin();
        Map<String, BeanInfo> allBean = iocPlugin.getAllBean();
        CglibWebIntercepter cglibWebIntercepter = new CglibWebIntercepter();
        JdkWebInvocationHandler jdkWebInvocationHandler = new JdkWebInvocationHandler();
        allBean.forEach((key, val) -> {
            BeanInfo beanInfo = val;
            Class<?> cls = beanInfo.getCls();
            if (isEnhance(cls)) {
                Class<?>[] interfaces = cls.getInterfaces();
                ClassLoader classLoader = cls.getClassLoader();
                try {
                    if (0 == interfaces.length) {
                        // 调用Cglib的代理
                        Enhancer enhancer = new Enhancer();
                        enhancer.setCallback(cglibWebIntercepter);
                        enhancer.setSuperclass(cls);
                        enhancer.setClassLoader(classLoader);
                        Object o = enhancer.create();
                        buildObject(cls, allBean, iocPlugin, o);
                        beanInfo.setObject(o);
                    } else {
                        // 调用Jdk的代理
                        beanInfo.setObject(buildObject(cls, allBean, iocPlugin, Proxy.newProxyInstance(classLoader, interfaces, jdkWebInvocationHandler)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            allBean.put(key, beanInfo);
        });

        iocPlugin.setBeans(allBean);
    }

    public Object buildObject(Class<?> cls, Map<String, BeanInfo> allBean, IocPlugin iocPlugin, Object o) throws IllegalAccessException {
        Field[] declaredFields = cls.getDeclaredFields();
        Class<Injection> injectionClass = Injection.class;

        for (int i = 0; i < declaredFields.length; ++i) {
            Field field = declaredFields[i];
            Class<?> fieldClass = field.getType();
            if (field.isAnnotationPresent(injectionClass) && allBean.containsKey(fieldClass.getName())) {
                BeanInfo info = allBean.get(fieldClass.getName());
                Object infoObject = info.getObject();
                if (infoObject == null) {
                    infoObject = iocPlugin.getBean(fieldClass);
                    if (null == infoObject) {
                        continue;
                    }
                }
                field.setAccessible(true);
                field.set(o, infoObject);
            }
        }
        return o;
    }

    private boolean isEnhance(Class<?> cls) {
        boolean flag = false;
        if (cls.isAnnotationPresent(after)
                || cls.isAnnotationPresent(before)
                || cls.isAnnotationPresent(surround)
                || cls.isAnnotationPresent(exception)) {
            flag = true;
        }
        if (!flag) {
            // 判断方法上是否有增强
            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(after)
                        || method.isAnnotationPresent(before)
                        || method.isAnnotationPresent(surround)
                        || method.isAnnotationPresent(exception)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

}
