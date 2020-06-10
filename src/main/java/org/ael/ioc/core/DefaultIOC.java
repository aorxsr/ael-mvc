package org.ael.ioc.core;

import lombok.Data;
import org.ael.commons.StringUtils;
import org.ael.http.WebContent;
import org.ael.ioc.core.annotation.Injection;
import org.ael.orm.annotation.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class DefaultIOC implements IOC {

    private Map<String, BeanInfo> iocs = new ConcurrentHashMap<>(16);

    private Set<Class> beanClss = new HashSet<>(8);

    public void addBeanClss(Class<? extends Annotation> cls) {
        this.beanClss.add(cls);
    }

    public void init(Set<Class<?>> clss) {
        if (beanClss.isEmpty()) return;
        for (Class<?> cls : clss) {
            for (Class aClass : beanClss) {
                if (cls.isAnnotationPresent(aClass)) {
                    iocs.put(cls.getName(), BeanInfo.builder().cls(cls).build());
                }
            }
        }
    }

    public Object getBean(Class<?> tClass) {
        String name = tClass.getName();
        if (iocs.containsKey(name)) {
            BeanInfo beanInfo = iocs.get(name);
            Object object = beanInfo.getObject();
            if (null == object) {
                try {
                    Class<?> cls = beanInfo.getCls();
                    object = cls.newInstance();
                    Field[] declaredFields = cls.getDeclaredFields();
                    Class<Injection> injectionClass = Injection.class;
                    Class<Value> valueClass = Value.class;
                    for (int i = 0; i < declaredFields.length; i++) {
                        Field field = declaredFields[i];
                        Class<?> fieldClass = field.getType();
                        if (field.isAnnotationPresent(injectionClass)) {
                            // 查询
                            if (iocs.containsKey(fieldClass.getName())) {
                                BeanInfo info = iocs.get(fieldClass.getName());
                                Object infoObject = info.getObject();
                                if (infoObject == null) {
                                    infoObject = getBean(fieldClass);
                                    if (null == infoObject) {
                                        continue;
                                    }
                                }
                                field.setAccessible(true);
                                field.set(object, infoObject);
                            }
                        } else if (field.isAnnotationPresent(valueClass)) {
                            Value value = field.getAnnotation(valueClass);
                            String valueName = value.name();
                            if (StringUtils.isNotEmpty(valueName)) {
                                field.setAccessible(true);
                                field.set(object, WebContent.ael.getEnvironment().getString(valueName, null));
                            }
                        }
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return object;
            } else {
                return object;
            }
        } else {
            return null;
        }
    }

}
