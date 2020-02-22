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
package org.ael.route.hook;

import lombok.extern.slf4j.Slf4j;
import org.ael.commons.StringUtils;
import org.ael.http.WebContent;
import org.ael.route.RouteHandler;
import org.ael.route.hook.annotation.HookController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author aorxsr
 * @date 2020/2/22
 */
@Slf4j
public class HookHandler {

    private ConcurrentHashMap<Pattern, Hook> HOOKS = new ConcurrentHashMap<>(16);

    public void initHook(Set<Class<?>> scanClass) {
        try {
            Class<HookController> hookControllerClass = HookController.class;
            Class<org.ael.route.hook.annotation.Hook> hookClass = org.ael.route.hook.annotation.Hook.class;
            for (Class<?> clazz : scanClass) {
                if (clazz.isAnnotationPresent(hookControllerClass)) {
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(hookClass)) {
                            org.ael.route.hook.annotation.Hook hook = method.getAnnotation(hookClass);
                            String url = hook.url();
                            if (StringUtils.isEmpty(url)) {
                                throw new RuntimeException("该hook没有设置url...");
                            }
                            HOOKS.put(Pattern.compile(RouteHandler.urlToPattern(url)), Hook.builder().method(method).target(clazz.newInstance()).build());
                        }
                    }
                }
            }
            log.info("hooks size : " + HOOKS.size());
        } catch (Exception e) {
            log.error("私有构造方法????????");
        }
    }

    public HookContext executeHooks(WebContent webContent) throws InvocationTargetException, IllegalAccessException {
        String uri = webContent.getRequest().getUri();
        List<Hook> hooks = new LinkedList<>();
        HOOKS.forEach((k, v) -> {
            if (k.matcher(uri).matches()) {
                hooks.add(v);
            }
        });

        for (Hook hook : hooks) {
            Object target = hook.getTarget();
            Method method = hook.getMethod();

            HookContext context = (HookContext) method.invoke(target, webContent);
            if (context.getHookReturnENUM() == HookContext.HookReturnENUM.SUCCESS) {
                continue;
            } else if (context.getHookReturnENUM() == HookContext.HookReturnENUM.FAILED) {
                return context;
            }
        }
        return new HookContext();
    }

}
