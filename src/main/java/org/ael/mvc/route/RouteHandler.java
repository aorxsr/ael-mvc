package org.ael.mvc.route;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.Ael;
import org.ael.mvc.annotation.*;
import org.ael.mvc.commons.StringUtils;
import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.constant.HttpMethodConstant;
import org.ael.mvc.constant.RouteTypeConstant;
import org.ael.mvc.container.exception.RequestParamRequiredException;
import org.ael.mvc.handler.init.AbstractInitHandler;
import org.ael.mvc.http.Request;
import org.ael.mvc.http.Response;
import org.ael.mvc.http.WebContent;
import org.ael.mvc.http.body.StringBody;
import org.ael.mvc.http.body.ViewBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:57
 */
@Slf4j
@Configuration(order = 2)
public class RouteHandler extends AbstractInitHandler {

    private Ael ael;
    private ConcurrentHashMap<String, RouteFunctionHandler> handlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Route> routeHandlers = new ConcurrentHashMap<>();

    @Override
    public void init(Ael ael) {
        this.ael = ael;
        try {
            scanLocalCLass();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        } catch (InstantiationException e) {
            log.error(e.getMessage());
        }
        WebContent.ael = this.ael;
        WebContent.ael.setRouteHandler(this);
    }

    public void scanLocalCLass() throws IllegalAccessException, InstantiationException {
        for (Class<?> clazz : ael.getScanClass()) {
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
                        addRoute(HttpMethodConstant.POST_UPPER, controllerUrl + methodUrl, clazz, method, getMethod.contentType());
                    }
                } else {
                    // Get方法,
                    String methodUrl = getMethod.value();
                    if (StringUtil.isNullOrEmpty(methodUrl)) {
                        continue;
                    }
                    addRoute(HttpMethodConstant.GET_UPPER, controllerUrl + methodUrl, clazz, method, getMethod.contentType());
                }
            }
        }
    }

    private void addRoute(String methodType, String newUrl, Class<?> clazz, Method method, String contentType) throws IllegalAccessException, InstantiationException {
        routeHandlers.put(methodType + HttpConstant.WELL + newUrl, Route.builder()
                .target(clazz.newInstance())
                .classType(clazz)
                .httpMethod(HttpMethodConstant.POST_UPPER)
                .path(newUrl)
                .routeType(RouteTypeConstant.ROUTE_TYPE_CLASS)
                .method(method)
                .contentType(contentType)
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
            webContent = WebContent.ael.getStaticsResourcesHandler().rander(webContent);
        } else {
            if (WebContent.ael.getRouteHandler().routeHandlers.containsKey(key)) {
                // enhance constant
                Route route = WebContent.ael.getRouteHandler().routeHandlers.get(key);
                if (RouteTypeConstant.ROUTE_TYPE_FUNCTION == route.getRouteType()) {
                    route.getRouteFunctionHandler().handler(webContent);
                } else if (RouteTypeConstant.ROUTE_TYPE_CLASS == route.getRouteType()) {
                    try {
                        route.getMethod().invoke(route.getTarget(), webContent);
                    } catch (IllegalAccessException e) {
                        log.info(e.getMessage());
                    } catch (InvocationTargetException e) {
                        log.info(e.getMessage());
                    }
                } else {
                    response.text(" No route type " + route.getRouteType());
                }
            } else {
                response.setStatus(500);
                response.text(" No Mapping " + uri);
            }
        }

        return webContent;
    }

    private Response invokeGet(Request request, Response response, WebContent webContent, Route route) throws InvocationTargetException, IllegalAccessException {
        Method routeMethod = route.getMethod();
        Parameter[] parameters = routeMethod.getParameters();
        List<Object> paramList = new ArrayList<>();

        for (Parameter parameter : parameters) {
            RequestParam annotation = parameter.getAnnotation(RouteConstant.REQUEST_PARAM_CLASS);
            // 名称
            String value;
            if (annotation == null) {
                value = parameter.getName();
            } else {
                value = annotation.value();
                if (StringUtils.isEmpty(value)) {
                    // 获取参数名称
                    value = parameter.getName();
                }
            }
            Object val = request.getPathParam(value);
            if (annotation.required()) {
                // check required
                if (null == val) {
                    throw new RequestParamRequiredException("request param name: " + value + " not found.");
                }
                // 判断是否是基本数据类型
                Class<?> parameterType = parameter.getType();
                if (isBasicType(parameterType)) {

                } else if (isObjectType(parameterType)) {
                    paramList.add(getObjectType(parameterType, webContent));
                }
            } else {
                // 取参数
                paramList.add(val);
            }
        }

        // 判断这个方法是否有 ResponseJson

        Object invoke = routeMethod.invoke(route.getTarget(), paramList.toArray());

        ResponseJson responseJson = routeMethod.getDeclaredAnnotation(RouteConstant.RESPONSE_JSON_CLASS);
        if (null == responseJson) {
            Class<?> returnType = routeMethod.getReturnType();
            if (String.class.equals(returnType)) {
                response.write(ViewBody.of(invoke.toString()));
            }
        } else {
            // 转JSON

            response.write(StringBody.of(""));
        }

        // 设置响应头
        response.setContentType(route.getContentType());
        return response;
    }

    private boolean isObjectType(Type type) {
        return type == Request.class ||
                type == Response.class ||
                type == WebContent.class ||
                type == ChannelHandlerContext.class;
    }

    private Object getObjectType(Type type, WebContent webContent) {
        if (type == Request.class) {
            return webContent.getRequest();
        } else if (type == Response.class) {
            return webContent.getResponse();
        } else if (type == ChannelHandlerContext.class) {
            return webContent.getCtx();
        } else {
            return null;
        }
    }

    private boolean isBasicType(Type type) {
        return type.equals(String.class) || type.equals(Integer.class) ||
                type.equals(Long.class) || type.equals(Double.class) ||
                type.equals(Float.class) || type.equals(Short.class) ||
                type.equals(Boolean.class) || type.equals(Byte.class) ||
                type.equals(Character.class) || type.equals(int.class) ||
                type.equals(long.class) || type.equals(double.class) ||
                type.equals(float.class) || type.equals(short.class) ||
                type.equals(boolean.class) || type.equals(byte.class) ||
                type.equals(char.class);
    }

    private boolean isStatics(String uri) {
        for (String resourcesHandler : WebContent.ael.getStaticsResourcesHandler().getResourcesHandlers()) {
            if (uri.startsWith(resourcesHandler)) {
                return true;
            }
        }
        return false;
    }

}
