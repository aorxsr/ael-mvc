package org.ael.route;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.c.annotation.*;
import org.ael.c.c.CHandler;
import org.ael.commons.StringUtils;
import org.ael.constant.RouteTypeConstant;
import org.ael.Ael;
import org.ael.http.Request;
import org.ael.http.Response;
import org.ael.http.WebContent;
import org.ael.http.body.EmptyBody;
import org.ael.route.asm.ASMUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:57
 */
@Slf4j
public class RouteHandler {

    public RouteHandler(Ael ael) {
        this.ael = ael;
        WebContent.ael = this.ael;
    }

    private Ael ael;
    private ConcurrentHashMap<String, RouteFunctionHandler> handlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Route> routeHandlers = new ConcurrentHashMap<>();

    /**
     * 1. URL
     * HttpMethod List<Pattern/>
     */
    // HttpMethod Reg
    private ConcurrentHashMap<String, List<RegexRoute>> rrMap = new ConcurrentHashMap<String, List<RegexRoute>>(16) {{
        put("GET", new ArrayList<>(16));
        put("POST", new ArrayList<>(16));
        put("PUT", new ArrayList<>(16));
        put("DELETE", new ArrayList<>(16));
    }};

    // UUID Route
    private ConcurrentHashMap<Integer, Route> routeMap = new ConcurrentHashMap<>(16);

    // AtomInt
    private AtomicInteger atomicInteger = new AtomicInteger();


    public void addHandler(String httpMethod, String url, RouteFunctionHandler routeFunctionHandler) {
        Route route = Route.builder()
                .httpMethod(httpMethod)
                .classType(routeFunctionHandler.getClass())
                .path(url)
                .routeFunctionHandler(routeFunctionHandler)
                .routeType(RouteTypeConstant.ROUTE_TYPE_FUNCTION)
                .build();
        putRegexAndRoute(url, rrMap.get(httpMethod), httpMethod, route);
    }

    private String urlToPattern(String url) {
        StringBuffer sub = new StringBuffer("^(/");
        // 分割
        String[] split = url.split("/");
        for (String s : split) {
            if (StringUtils.isNotEmpty(s)) {
                // 使用? PathParam
                if (s.contains("?")) {
                    sub.append("([^/]+)/");
                } else {
                    sub.append(s + "/");
                }
            }
        }
        sub.deleteCharAt(sub.length() - 1);
        sub.append(")$");
        return sub.toString();
    }


    public void init() {
        try {
            CHandler cHandler = new CHandler(ael);
            ConcurrentHashMap<String, Route> execute = cHandler.execute();
            Iterator<String> keyIterator = execute.keySet().iterator();
            while (keyIterator.hasNext()) {
                String next = keyIterator.next();

                Route route = execute.get(next);
                String httpMethod = route.getHttpMethod();

                putRegexAndRoute(route.getPath(), rrMap.get(httpMethod), httpMethod, route);
            }
            routeHandlers.forEach((k, v) -> log.info(k));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void putRegexAndRoute(String path, List<RegexRoute> regexRoutes, String httpMethod, Route route) {
        String pattern = urlToPattern(path);
        int atomInt = atomicInteger.getAndIncrement();
        RegexRoute regexRoute = RegexRoute
                .builder()
                .pattern(Pattern.compile(pattern))
                .atomInt(atomInt)
                .build();
        regexRoutes.add(regexRoute);
        rrMap.put(httpMethod, regexRoutes);
        routeMap.put(atomInt, route);

        log.info(httpMethod + "#" + path);
    }

    // ^(/api_test/([^/]+))$
    private Integer getAutomInt(String method, String url) {
        List<RegexRoute> regexRoutes = rrMap.get(method);
        Optional<RegexRoute> first = regexRoutes.stream()
                .filter(regexRoute -> regexRoute.matching(url))
                .findFirst();
        if (first.isPresent()) {
            return first.get().getAtomInt();
        } else {
            return null;
        }
    }

    public WebContent executeHandler(WebContent webContent) {
        Request request = webContent.getRequest();
        Response response = webContent.getResponse();
        String uri = request.getUri();
        try {
            // 判断是否是 静态资源文件...
            if (isStatics(uri)) {
                webContent = WebContent.ael.getStaticsResourcesHandler().rander(webContent);
            } else {
                Integer automInt = getAutomInt(request.getMethod(), uri);
                if (null != automInt) {
                    Route route = routeMap.get(automInt);
                    if (RouteTypeConstant.ROUTE_TYPE_FUNCTION == route.getRouteType()) {
                        route.getRouteFunctionHandler().handler(webContent);
                    } else if (RouteTypeConstant.ROUTE_TYPE_CLASS == route.getRouteType()) {
                        try {
                            Method method = route.getMethod();
                            String[] methodParamNames = ASMUtils.getMethodParamNames(route.getClassType(), method);
                            Parameter[] parameters = method.getParameters();
                            Object[] objects = new Object[parameters.length];

                            for (int i = 0; i < methodParamNames.length; i++) {
                                Parameter parameter = parameters[i];
                                String paramName = methodParamNames[i];
                                Type type = parameter.getParameterizedType();
                                if (isAnnParam(parameter)) {
                                    objects[i] = getAnnParam(request, parameter, paramName);
                                }
                                if (isBasicType(type)) {
                                    List<String> value = (List<String>) request.getParameter(paramName);
                                    if (null == value) {
                                        value = (List<String>) request.getPathParam(paramName);
                                    }
                                    if (value.isEmpty()) {
                                        objects[i] = null;
                                    } else {
                                        objects[i] = value.get(0);
                                    }
                                }
                                if (isObjectType(type)) {
                                    objects[i] = getObjectType(type, webContent);
                                }
                            }
                            Object invoke = method.invoke(ael.getIocPlugin().getBean(route.getClassType()), objects);

                            ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
                            if (null == responseBody) {
                                // 判断是否有返回值
                                if (null == invoke) {
                                    // 只返回响应头
                                    response.write(new EmptyBody());
                                } else {
                                    if (invoke instanceof String) {
                                        response.html(invoke.toString());
                                    } else {
                                        throw new RuntimeException("不确定的返回值");
                                    }
                                }
                            } else {
                                if (invoke instanceof String) {
                                    response.json(invoke.toString());
                                } else {
                                    response.json(JSONObject.toJSONString(invoke));
                                }
                            }
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
        } catch (Exception e) {
            response.setStatus(500);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            response.json(sw.toString());
        }

        return webContent;
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

    private boolean isAnnParam(Parameter parameter) {
        return parameter.isAnnotationPresent(PathParam.class)
                || parameter.isAnnotationPresent(RequestParam.class)
                || parameter.isAnnotationPresent(RequestBody.class)
                || parameter.isAnnotationPresent(MultiPartFileParam.class);
    }

    private Object annTro(boolean flag, String parameterName) {
        if (flag) {
            throw new RuntimeException("没有找到参数:" + parameterName);
        }
        return null;
    }

    private Object getAnnParam(Request request, Parameter parameter, String paramName) {
        Object value = null;
        if (parameter.isAnnotationPresent(RequestBody.class)) {
            String body = request.body().toString(CharsetUtil.UTF_8);
            RequestBody requestBody = parameter.getDeclaredAnnotation(RequestBody.class);
            if (StringUtils.isEmpty(body)) {
                annTro(requestBody.required(), paramName);
            } else {
                value = JSONObject.parseObject(body, parameter.getType());
            }
        } else if (parameter.isAnnotationPresent(PathParam.class)) {
            PathParam pathParam = parameter.getDeclaredAnnotation(PathParam.class);
            String name = pathParam.name();
            if (StringUtils.isEmpty(name)) {
                name = paramName;
            }
            value = request.getPathParam(name);
            if (value == null) {
                annTro(pathParam.required(), name);
            }
        } else if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
            String name = requestParam.value();
            if (StringUtils.isEmpty(name)) {
                name = paramName;
            }
            value = request.getParameter(name);
            if (value == null) {
                annTro(requestParam.required(), name);
            }
        } else if (parameter.isAnnotationPresent(MultiPartFileParam.class)) {
            MultiPartFileParam multiPartFileParam = parameter.getDeclaredAnnotation(MultiPartFileParam.class);
            String name = multiPartFileParam.name();
            if (StringUtils.isEmpty(name)) {
                name = paramName;
            }
            value = request.getMultiPartFile(name);
            if (value == null) {
                annTro(multiPartFileParam.required(), name);
            }
        }

        return value;
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
