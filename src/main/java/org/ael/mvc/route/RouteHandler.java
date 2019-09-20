package org.ael.mvc.route;

import cn.hutool.core.util.ClassUtil;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.Ael;
import org.ael.mvc.annotation.Controller;
import org.ael.mvc.annotation.GetMapping;
import org.ael.mvc.annotation.PostMapping;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.constant.HttpMethodConstant;
import org.ael.mvc.constant.RouteTypeConstant;
import org.ael.mvc.exception.ViewNotFoundException;
import org.ael.mvc.http.Request;
import org.ael.mvc.http.Response;
import org.ael.mvc.http.WebContent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:57
 */
@Slf4j
public class RouteHandler {

	private Ael ael;

	public void initRouteHandler(Ael ael) {
		this.ael = ael;
		try {
			scanLocalCLass();
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (InstantiationException e) {
			log.error(e.getMessage());
		}
	}

	private ConcurrentHashMap<String, RouteFunctionHandler> handlers = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Route> routeHandlers = new ConcurrentHashMap<>();

	public void scanLocalCLass() throws IllegalAccessException, InstantiationException {
		String scanPackage = ael.getEnvironment().getString(EnvironmentConstant.SCAN_PACKAGE);
		if (StringUtil.isNullOrEmpty(scanPackage)) {
			scanPackage = ael.getStartClass().getPackage().getName();
			if (StringUtil.isNullOrEmpty(scanPackage)) {
				return;
			}
		}

		Set<Class<?>> classes = ClassUtil.scanPackage(scanPackage);
		for (Class<?> clazz : classes) {
			Controller controller = clazz.getAnnotation(Controller.class);
			if (null == controller) {
				continue;
			}

			String controllerUrl;

			GetMapping getMapping = clazz.getAnnotation(GetMapping.class);
			PostMapping postMapping = clazz.getAnnotation(PostMapping.class);
			if (null == getMapping) {
				if (null == postMapping) {
					continue;
				} else {
					controllerUrl = postMapping.value();
				}
			} else {
				controllerUrl = getMapping.value();
			}

			// 获取所有方法
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
						addRoute(HttpMethodConstant.POST_UPPER, controllerUrl + methodUrl, clazz, method);
					}
				} else {
					// Get方法,
					String methodUrl = getMethod.value();
					if (StringUtil.isNullOrEmpty(methodUrl)) {
						continue;
					}
					addRoute(HttpMethodConstant.GET_UPPER, controllerUrl + methodUrl, clazz, method);
				}
			}
		}
	}

	private void addRoute(String methodType, String newUrl, Class<?> clazz, Method method) throws IllegalAccessException, InstantiationException {
		routeHandlers.put(methodType + HttpConstant.WELL + newUrl, Route.builder()
				.target(clazz.newInstance())
				.classType(clazz)
				.httpMethod(HttpMethodConstant.POST_UPPER)
				.path(newUrl)
				.routeType(RouteTypeConstant.ROUTE_TYPE_CLASS)
				.method(method)
				.build());

		log.info(methodType + " : " + newUrl);
	}


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

		String method = request.getMethod();
		String key = method.toUpperCase() + "#" + uri;

		// 判断是否是 静态资源文件...
		if (isStatics(uri)) {
			webContent = ael.getStaticsResourcesHandler().rander(webContent);
		} else {
			if (routeHandlers.containsKey(key)) {
				Route route = routeHandlers.get(key);
				if (RouteTypeConstant.ROUTE_TYPE_FUNCTION == route.getRouteType()) {
					route.getRouteFunctionHandler().handler(webContent);
				} else if (RouteTypeConstant.ROUTE_TYPE_CLASS == route.getRouteType()) {
					try {
						route.getMethod().invoke(route.getTarget(), webContent);
						// 判断参数是否正确
						// 获取请求参数
//						if (method.equalsIgnoreCase(HttpMethodConstant.GET_UPPER)) {
//							// 取URL参数
//							response.text("GET正在研发中.");
//						} else if (method.equalsIgnoreCase(HttpMethodConstant.POST_UPPER)) {
//							// 取body参数
//							response.text("POST正在研发中.");
//						} else {
//							response.text("不支持GET|POST以外的方法.");
//						}
					} catch (IllegalAccessException e) {
						log.info(e.getMessage());
					} catch (InvocationTargetException e) {
						log.info(e.getMessage());
					}
				} else {
					response.text(" No route type " + route.getRouteType());
				}
			} else {
				// 返回404
				response.setStatus(500);
				response.text(" No Mapping " + uri);
			}
		}

		return webContent;
	}

	private boolean isStatics(String uri) {
		for (String resourcesHandler : ael.getStaticsResourcesHandler().getResourcesHandlers()) {
			if (uri.startsWith(resourcesHandler)) {
				return true;
			}
		}
		return false;
	}

}
