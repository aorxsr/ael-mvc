package org.ael.mvc;

import lombok.Getter;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.constant.HttpMethodConstant;
import org.ael.mvc.handler.StaticsResourcesHandler;
import org.ael.mvc.http.session.SessionHandler;
import org.ael.mvc.http.session.SessionManager;
import org.ael.mvc.route.RouteFunctionHandler;
import org.ael.mvc.route.RouteHandler;
import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.NettyServer;
import org.ael.mvc.template.AelTemplate;
import org.ael.mvc.template.give.DefaultTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
public class Ael {

	@Getter
	private Class<?> startClass;

	@Getter
	private AelTemplate aelTemplate = new DefaultTemplate();

	@Getter
	private StaticsResourcesHandler staticsResourcesHandler = new StaticsResourcesHandler();

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
		routeHandler.initRouteHandler(this);
		server.start(this);
		return this;
	}

	public Ael start(Class<?> startClass) {
		this.startClass = startClass;
		routeHandler.initRouteHandler(this);
		server.start(this);
		return this;
	}

	public Ael setSessionKey(String sessionKey) {
		environment.setProperty(EnvironmentConstant.SESSION_KEY, sessionKey);
		return this;
	}

	public Ael setFilePath(String filePath) {
		environment.setProperty(EnvironmentConstant.ENVIRONMENT_FILE, filePath);
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

}
