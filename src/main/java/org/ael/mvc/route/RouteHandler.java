package org.ael.mvc.route;

import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.constant.RouteTypeConstant;
import org.ael.mvc.http.Request;
import org.ael.mvc.http.Response;
import org.ael.mvc.http.WebContent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:57
 */
@Slf4j
public class RouteHandler {

	private ConcurrentHashMap<String, RouteFunctionHandler> handlers = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Route> routeHandlers = new ConcurrentHashMap<>();

	public void addHandler(String method, String url, RouteFunctionHandler routeFunctionHandler) {
		String newUrl = method.toUpperCase() + "#" + url;
		log.info(method + " : " + url);

		if (routeHandlers.containsKey(newUrl)) {
			log.error("url:" + newUrl + " contains...");
		} else {
			// put
			handlers.put(newUrl, routeFunctionHandler);
			routeHandlers.put(newUrl, Route.builder()
					.httpMethod(method)
					.classType(routeFunctionHandler.getClass())
					.path(url)
					.routeFunctionHandler(routeFunctionHandler)
					.routeType(RouteTypeConstant.ROUTE_TYPE_FUNCTION)
					.build());
		}

	}

	public WebContent executeHandler(WebContent webContent) {
		Request request = webContent.getRequest();
		Response response = webContent.getResponse();

		String uri = request.getUri();

		String key = request.getMethod().toUpperCase() + "#" + uri;

		if (routeHandlers.containsKey(key)) {
			Route route = routeHandlers.get(key);
			if (RouteTypeConstant.ROUTE_TYPE_FUNCTION == route.getRouteType()) {
				route.getRouteFunctionHandler().handler(webContent);
			} else if (RouteTypeConstant.ROUTE_TYPE_CLASS == route.getRouteType()) {
				response.text(" No route type " + route.getRouteType());
			} else {
				response.text(" No route type " + route.getRouteType());
			}
		} else {
			// 返回404
			response.setStatus(500);
			response.text(" No Mapping " + uri);
		}

		return webContent;
	}

}
