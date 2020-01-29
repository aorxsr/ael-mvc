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

import cn.hutool.core.util.ClassUtil;
import io.netty.util.internal.StringUtil;
import org.ael.Ael;
import org.ael.c.annotation.Controller;
import org.ael.c.annotation.GetMapping;
import org.ael.c.annotation.PostMapping;
import org.ael.c.annotation.RequestMapping;
import org.ael.commons.ClassUtils;
import org.ael.constant.EnvironmentConstant;
import org.ael.constant.HttpConstant;
import org.ael.constant.HttpMethodConstant;
import org.ael.constant.RouteTypeConstant;
import org.ael.route.Route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
        List<String> scans = ael.getEnvironment().getList(EnvironmentConstant.SCAN_PACKAGE, new ArrayList());
        if (scans.isEmpty()) {
            return routeHandlers;
        }
        List<Class<?>> classs = ClassUtils.getClasss(scans, Controller.class);

        for (Class<?> clazz : classs) {
            String controllerUrl = null;
            RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            if (null == requestMapping) {
                continue;
            } else {
                if (requestMapping.value().isEmpty()) {
                    controllerUrl = requestMapping.value();
                }
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                GetMapping getMethod = method.getAnnotation(GetMapping.class);
                if (null == getMethod) {
                    PostMapping postMethod = method.getAnnotation(PostMapping.class);
                    if (null == postMethod) {
                        continue;
                    } else {
                        String methodUrl = postMethod.value();
                        if (StringUtil.isNullOrEmpty(methodUrl)) {
                            continue;
                        }
                        routeHandlers.put(HttpMethodConstant.POST_UPPER + HttpConstant.WELL + controllerUrl, Route.builder()
                                .target(clazz.newInstance())
                                .classType(clazz)
                                .httpMethod(HttpMethodConstant.POST_UPPER)
                                .path(controllerUrl)
                                .routeType(RouteTypeConstant.ROUTE_TYPE_CLASS)
                                .method(method)
                                .contentType(getMethod.contentType())
                                .build());

                    }
                } else {
                    // Get方法,
                    String methodUrl = getMethod.value();
                    if (StringUtil.isNullOrEmpty(methodUrl)) {
                        continue;
                    }
                    if (methodUrl.startsWith("/")) {
                        controllerUrl += methodUrl;
                    } else {
                        controllerUrl = "/" + methodUrl;
                    }
                    routeHandlers.put(HttpMethodConstant.GET_UPPER + HttpConstant.WELL + controllerUrl, Route.builder()
                            .target(clazz.newInstance())
                            .classType(clazz)
                            .httpMethod(HttpMethodConstant.POST_UPPER)
                            .path(controllerUrl)
                            .routeType(RouteTypeConstant.ROUTE_TYPE_CLASS)
                            .method(method)
                            .contentType(getMethod.contentType())
                            .build());
                }
            }
        }
        return routeHandlers;
    }

    private void buildRoute() {
//        String methodUrl = getMethod.value();
//        if (StringUtil.isNullOrEmpty(methodUrl)) {
//            continue;
//        }
//        if (methodUrl.startsWith("/")) {
//            controllerUrl += methodUrl;
//        } else {
//            controllerUrl = "/" + methodUrl;
//        }
//        routeHandlers.put(HttpMethodConstant.GET_UPPER + HttpConstant.WELL + controllerUrl, Route.builder()
//                .target(clazz.newInstance())
//                .classType(clazz)
//                .httpMethod(HttpMethodConstant.POST_UPPER)
//                .path(controllerUrl)
//                .routeType(RouteTypeConstant.ROUTE_TYPE_CLASS)
//                .method(method)
//                .contentType(getMethod.contentType())
//                .build());
    }

}
