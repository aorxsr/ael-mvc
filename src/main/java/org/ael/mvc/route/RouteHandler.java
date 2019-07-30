package org.ael.mvc.route;

import lombok.extern.slf4j.Slf4j;
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

	public void addHandler(String method, String url, RouteFunctionHandler routeFunctionHandler) {
		String newUrl = method.toUpperCase() + "#" + url;
		log.info(method + " : " + url);
		handlers.put(newUrl, routeFunctionHandler);
	}

	public WebContent executeHandler(WebContent webContent) {
		Request request = webContent.getRequest();
		Response response = webContent.getResponse();

		String key = request.getMethod().toUpperCase() + "#" + request.getUri();

		if (handlers.containsKey(key)) {
			RouteFunctionHandler routeFunctionHandler = handlers.get(key);
			routeFunctionHandler.handler(webContent);
		} else {
			// 返回404
			response.setStatus(404);
		}

		return webContent;
	}

}
