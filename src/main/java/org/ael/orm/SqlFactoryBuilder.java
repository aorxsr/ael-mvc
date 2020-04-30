package org.ael.orm;

import net.sf.cglib.proxy.Enhancer;
import org.ael.Ael;
import org.ael.ioc.core.BeanInfo;
import org.ael.orm.annotation.Dao;
import org.ael.orm.annotation.dml.Delete;
import org.ael.orm.annotation.dml.Insert;
import org.ael.orm.annotation.dml.Query;
import org.ael.orm.annotation.dml.Update;
import org.ael.orm.proxy.CglibDataSourceProxy;
import org.ael.orm.proxy.JdkDataSourceProxy;
import org.ael.plugin.aop.AopPlugin;
import org.ael.plugin.ioc.IocPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;

public class SqlFactoryBuilder {

    public void init(Ael ael) {
        AopPlugin aopPlugin = ael.getAopPlugin();
        IocPlugin iocPlugin = ael.getIocPlugin();
        Map<String, BeanInfo> allBean = iocPlugin.getAllBean();
        Class<Dao> daoClass = Dao.class;
        JdkDataSourceProxy jdkProxy = new JdkDataSourceProxy();
        CglibDataSourceProxy cglibProxy = new CglibDataSourceProxy();

        allBean.forEach((key, val) -> {
            Class<?> cls = val.getCls();
            if (cls.isAnnotationPresent(daoClass)) {
                Class<?>[] interfaces = cls.getInterfaces();
//                Method[] declaredMethods = cls.getDeclaredMethods();
//                for (Method method : declaredMethods) {
//                    if (isDml(method)) {
                try {
//                    if (0 == interfaces.length) {
//                        // jdk proxy
                    if (0 == interfaces.length) {
                        interfaces = Arrays.copyOf(interfaces, 1);
                        interfaces[0] = cls;
                    }
                        Object instance = Proxy.newProxyInstance(cls.getClassLoader(), interfaces, jdkProxy);
                        aopPlugin.buildObject(cls, allBean, iocPlugin, instance);
                        val.setObject(instance);



//                    } else {
                        // cglib proxy
                       /* Enhancer enhancer = new Enhancer();
                        enhancer.setCallback(cglibProxy);
                        enhancer.setSuperclass(cls.getClass());
//                        enhancer.setClassLoader(cls.getClassLoader());
                        Object instance = enhancer.create();
                        val.setObject(instance);*/
//                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
//                    }
//                }
            }
            allBean.put(key, val);
        });
        iocPlugin.setBeans(allBean);
    }

//        public boolean isDml (Method method){
//            return method.isAnnotationPresent(Delete.class)
//                    || method.isAnnotationPresent(Insert.class)
//                    || method.isAnnotationPresent(Update.class)
//                    || method.isAnnotationPresent(Query.class);
//        }

}
