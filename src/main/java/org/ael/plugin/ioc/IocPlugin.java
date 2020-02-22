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
package org.ael.plugin.ioc;

import org.ael.Ael;
import org.ael.c.annotation.Controller;
import org.ael.http.WebContent;
import org.ael.http.inter.Request;
import org.ael.http.inter.Response;
import org.ael.ioc.core.BeanInfo;
import org.ael.ioc.core.DefaultIOC;
import org.ael.ioc.core.annotation.Bean;
import org.ael.ioc.core.annotation.Injection;
import org.ael.ioc.core.annotation.Service;
import org.ael.plugin.aop.AopPlugin;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author aorxsr
 * @Data 2020/2/6
 */
public class IocPlugin {

    private DefaultIOC ioc = new DefaultIOC();

    public void initIoc(Ael ael) {
        ioc.addBeanClss(Bean.class);
        ioc.addBeanClss(Service.class);
        ioc.addBeanClss(Controller.class);

        ioc.init(ael.getScanClass());
    }

    public Object getBean(Class<?> tClass) {
        return ioc.getBean(tClass);
    }

    public Map<String, BeanInfo> getAllBean() {
        return ioc.getIocs();
    }

    public void setBeans(Map<String, BeanInfo> beans) {
        ioc.setIocs(beans);
    }

    public Object buildObject(Class<?> objClass, Object object) throws IllegalAccessException {
        Class<Injection> injectionClass = Injection.class;

        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(injectionClass)) {

                WebContent webContent = AopPlugin.WEB_CONTENT_THREAD_LOCAL.get();

                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                // 判断是否是WebContent或者其他的东西
                if (fieldType == WebContent.class
                        || fieldType == Request.class
                        || fieldType == Response.class) {
                    if (fieldType == WebContent.class) {
                        field.set(object, webContent);
                    } else if (fieldType == Request.class) {
                        field.set(object, webContent.getResponse());
                    } else if (fieldType == Response.class) {
                        field.set(object, webContent.getResponse());
                    }
                }
                Object bean = getBean(fieldType);
                if (null != bean) {
                    field.set(object, bean);
                }
            }
        }

        return object;
    }

}
