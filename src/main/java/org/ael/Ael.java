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
package org.ael;

import lombok.Data;
import org.ael.handler.StaticsResourcesHandler;
import org.ael.http.session.SessionHandler;
import org.ael.http.session.SessionManager;
import org.ael.constant.EnvironmentConstant;
import org.ael.constant.HttpMethodConstant;
import org.ael.orm.SqlFactoryBuilder;
import org.ael.plugin.aop.AopPlugin;
import org.ael.plugin.ioc.IocPlugin;
import org.ael.route.function.FunctionRouteHandler;
import org.ael.route.function.RouteFunctionHandler;
import org.ael.route.RouteHandler;
import org.ael.route.hook.HookHandler;
import org.ael.server.Server;
import org.ael.server.netty.InitialHandler;
import org.ael.server.netty.NettyServer;
import org.ael.server.netty.exception.ExecuteException;
import org.ael.template.AelTemplate;
import org.ael.template.give.DefaultTemplate;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
@Data
public class Ael {

    private AelTemplate aelTemplate = new DefaultTemplate();

    private StaticsResourcesHandler staticsResourcesHandler = new StaticsResourcesHandler();

    private Server server = new NettyServer();

    private Environment environment = new Environment();

    private RouteHandler routeHandler = new RouteHandler(this);

    private SessionManager sessionManager = new SessionManager();

    private SessionHandler sessionHandler = new SessionHandler(sessionManager);

    private Set<Class<?>> scanClass = new LinkedHashSet<>(16);

    private List<InitialHandler> initHandlers = new ArrayList<>();

    private IocPlugin iocPlugin = new IocPlugin();

    private AopPlugin aopPlugin = new AopPlugin();

    private HookHandler hookHandler = new HookHandler();

    private FunctionRouteHandler functionRouteHandler = new FunctionRouteHandler();

    private SqlFactoryBuilder sqlFactoryBuilder = new SqlFactoryBuilder();

    private ExecuteException executeException = new ExecuteException();

    public Ael start() {
        server.start(this);
        return this;
    }

    public Ael setSessionKey(String sessionKey) {
        environment.put(EnvironmentConstant.SESSION_KEY, sessionKey);
        return this;
    }

    public void stop() {
        server.stop();
    }

    public Ael setTemplateImpl(Class<?> templateClass) throws IllegalAccessException, InstantiationException {
        aelTemplate = (AelTemplate) templateClass.newInstance();
        return this;
    }

    public Ael addResourcesMapping(String resourcesHandler, String resourcesLocation) {
        staticsResourcesHandler.getResources().put(resourcesHandler, resourcesLocation);
        staticsResourcesHandler.getResourcesHandlers().add(resourcesHandler);
        return this;
    }

    /**
     * 手动增加注解
     * @param cls
     * @return
     */
    public Ael addAnnotationScanPackage(Class<? extends Annotation> cls) {
        environment.getList(EnvironmentConstant.IOCKeyName, new ArrayList()).add(cls);
        return this;
    }

}