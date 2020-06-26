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
package org.ael.c.c;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.Ael;
import org.ael.c.annotation.Controller;
import org.ael.c.annotation.GetMapping;
import org.ael.c.annotation.PostMapping;
import org.ael.c.annotation.RequestMapping;
import org.ael.commons.ClassUtils;
import org.ael.commons.StringUtils;
import org.ael.constant.*;
import org.ael.route.Route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CHandler {

    /**
     * 1. 扫描所有Controller
     * 2. 进行注册
     */

    private Ael ael;

    private ConcurrentHashMap<String, Route> routeHandlers = new ConcurrentHashMap<>();

    public CHandler(Ael ael) {
        this.ael = ael;
    }

    public ConcurrentHashMap<String, Route> execute() throws IllegalAccessException, InstantiationException {
//        List<String> scans = ael.getEnvironment().getList(EnvironmentConstant.SCAN_PACKAGE, new ArrayList());
//        if (scans.isEmpty()) {
//            return routeHandlers;
//        }
        Set<Class<?>> classs = ael.getScanClass();

        for (Class<?> clazz : classs) {
            String controllerUrl = "";

            RequestMapping controllerRequestMapping = clazz.getAnnotation(RequestMapping.class);
            if (null != controllerRequestMapping) {
                if (StringUtils.isNotEmpty(controllerRequestMapping.value())) {
                    controllerUrl = controllerRequestMapping.value();
                }
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                GetMapping getMethod = method.getAnnotation(GetMapping.class);
                if (null == getMethod) {
                    PostMapping postMethod = method.getAnnotation(PostMapping.class);
                    if (null == postMethod) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        if (null == requestMapping) {
                            continue;
                        } else {

                            buildRoute(controllerUrl, requestMapping.value(), clazz, method, requestMapping.contentType(), HttpMethodConstant.ALL_UPPER);
                        }
                    } else {
                        buildRoute(controllerUrl, postMethod.value(), clazz, method, postMethod.contentType(), HttpMethodConstant.POST_UPPER);
                    }
                } else {
                    // get方法
                    String[] value = getMethod.value();
                    for (String url : value) {
                        if (StringUtils.isNotEmpty(url)) {
                            buildRoute(controllerUrl, url, clazz, method, getMethod.contentType(), HttpMethodConstant.GET_UPPER);
                        }
                    }
                }
            }
        }
        return routeHandlers;
    }

    private void buildRoute(String controllerUrl, String methodUrl, Class<?> clazz, Method method, String contentType, String httpMethod) throws IllegalAccessException, InstantiationException {
        if (StringUtil.isNullOrEmpty(methodUrl)) {
            return;
        }
        if (methodUrl.startsWith("/")) {
            controllerUrl += methodUrl;
        } else {
            controllerUrl = "/" + methodUrl;
        }
        routeHandlers.put(httpMethod + HttpConstant.WELL + controllerUrl, Route.builder()
                .target(clazz.newInstance())
                .classType(clazz)
                .httpMethod(httpMethod)
                .path(controllerUrl)
                .routeType(RouteTypeConstant.ROUTE_TYPE_CLASS)
                .method(method)
                .contentType(contentType)
                .build());
    }

}
