package org.ael.mvc;

import lombok.Getter;
import org.ael.mvc.constant.HttpMethodConstant;
import org.ael.mvc.http.session.SessionHandler;
import org.ael.mvc.http.session.SessionManager;
import org.ael.mvc.route.RouteFunctionHandler;
import org.ael.mvc.route.RouteHandler;
import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.NettyServer;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
public class Ael {

    @Getter
    private Class<?> startClass;

    private Server server = new NettyServer();
    @Getter
    private Environment environment = Environment.of();
    @Getter
    private RouteHandler routeHandler = new RouteHandler();
    @Getter
    private SessionManager sessionManager = new SessionManager();
    @Getter
    private SessionHandler sessionHandler = new SessionHandler(sessionManager);

    public Ael start() {
        server.start(this);
        return this;
    }

    public Ael start(Class<?> startClass) {
        this.startClass = startClass;
        server.start(this);
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

}
