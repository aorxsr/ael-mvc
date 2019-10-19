package org.ael.mvc;

import lombok.Data;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.constant.HttpMethodConstant;
import org.ael.mvc.container.SimpleContainer;
import org.ael.mvc.enhance.EnhanceHandler;
import org.ael.mvc.handler.StaticsResourcesHandler;
import org.ael.mvc.http.session.SessionHandler;
import org.ael.mvc.http.session.SessionManager;
import org.ael.mvc.route.RouteFunctionHandler;
import org.ael.mvc.route.RouteHandler;
import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.InitialHandler;
import org.ael.mvc.server.netty.NettyServer;
import org.ael.mvc.template.AelTemplate;
import org.ael.mvc.template.give.DefaultTemplate;

import java.util.*;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
@Data
public class Ael {

    private Class<?> startClass;

    private AelTemplate aelTemplate = new DefaultTemplate();

    private StaticsResourcesHandler staticsResourcesHandler = new StaticsResourcesHandler();

    private Server server = new NettyServer();

    private Environment environment = new Environment();

    private SimpleContainer container = new SimpleContainer(this);

    private RouteHandler routeHandler = new RouteHandler();

    private SessionManager sessionManager = new SessionManager();

    private SessionHandler sessionHandler = new SessionHandler(sessionManager);

    private Set<Class<?>> scanClass = new LinkedHashSet<>(16);

    private EnhanceHandler enhanceHandler;

    /**
     * 用于初始化
     */
    private List<InitialHandler> initHandlers = new ArrayList<>();

    public Ael start() {
        server.start(this);
        return this;
    }

    public Ael start(Class<?> startClass) {
        this.startClass = startClass;
        server.start(this);
        return this;
    }

    public Ael setSessionKey(String sessionKey) {
        environment.put(EnvironmentConstant.SESSION_KEY, sessionKey);
        return this;
    }

    public Ael setFilePath(String filePath) {
        environment.put(EnvironmentConstant.ENVIRONMENT_FILE, filePath);
        return this;
    }

    public void stop() {
        server.stop();
    }

    public Ael get(String url, RouteFunctionHandler handler) {
        routeHandler.addHandler(HttpMethodConstant.GET_UPPER, url, handler);
        return this;
    }

    public Ael post(String url, RouteFunctionHandler handler) {
        routeHandler.addHandler(HttpMethodConstant.POST_UPPER, url, handler);
        return this;
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

    public Ael addResourcesMappingAll(Map<String, String> mapping) {
        staticsResourcesHandler.getResources().putAll(mapping);
        staticsResourcesHandler.getResourcesHandlers().addAll(mapping.keySet());
        return this;
    }

    public Ael removeResourcesMapping(String resourcesHandler) {
        if (staticsResourcesHandler.getResources().containsKey(resourcesHandler)) {
            staticsResourcesHandler.getResources().remove(resourcesHandler);
            staticsResourcesHandler.getResourcesHandlers().remove(resourcesHandler);
        }
        return this;
    }

    public Ael removeResourcesMappingAll(Map<String, String> mapping) {
        mapping.forEach((resourceHandler, resourcesLocation) -> {
            if (staticsResourcesHandler.getResources().containsKey(resourceHandler)) {
                staticsResourcesHandler.getResources().remove(resourceHandler);
                staticsResourcesHandler.getResourcesHandlers().remove(resourceHandler);
            }
        });
        return this;
    }

    public void addScanClass(Set<Class<?>> classSet) {
        this.scanClass.addAll(classSet);
    }

    public void addScanClass(Class<?> startClass) {
        this.scanClass.add(startClass);
    }

}
