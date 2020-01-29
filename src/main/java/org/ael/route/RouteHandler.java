package org.ael.route;

import cn.hutool.core.util.IdUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.annotation.Configuration;
import org.ael.c.c.CHandler;
import org.ael.commons.StringUtils;
import org.ael.constant.RouteTypeConstant;
import org.ael.handler.init.AbstractInitHandler;
import org.ael.Ael;
import org.ael.http.Request;
import org.ael.http.Response;
import org.ael.http.WebContent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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
        List<RegexRoute> regexRoutes = rrMap.get(httpMethod);

        Route route = Route.builder()
                .httpMethod(httpMethod)
                .classType(routeFunctionHandler.getClass())
                .path(url)
                .routeFunctionHandler(routeFunctionHandler)
                .routeType(RouteTypeConstant.ROUTE_TYPE_FUNCTION)
                .build();
        putRegexAndRoute(url, regexRoutes, httpMethod, route);

        log.info(httpMethod + "#" + url);
    }

    private String urlToPattern(String url) {
        StringBuffer sub = new StringBuffer("(/");
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
        sub.append(")|");
        return sub.toString();
    }

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
        // console all url...
        routeHandlers.forEach((k, v) -> log.info(k));
    }

    public void scanLocalCLass() throws IllegalAccessException, InstantiationException {
        CHandler cHandler = new CHandler(ael);
        ConcurrentHashMap<String, Route> execute = cHandler.execute();
        Iterator<String> keyIterator = execute.keySet().iterator();
        while (keyIterator.hasNext()) {
            String next = keyIterator.next();

            Route route = execute.get(next);
            String httpMethod = route.getHttpMethod();

            List<RegexRoute> regexRoutes = rrMap.get(httpMethod);

            String path = route.getPath();
            // 将URL格式化成正则表达式
            putRegexAndRoute(path, regexRoutes, httpMethod, route);
        }
    }

    private void putRegexAndRoute(String path, List<RegexRoute> regexRoutes, String httpMethod, Route route) {
        String pattern = urlToPattern(path);
        int atomInt = atomicInteger.get();
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

        String method = request.getMethod();
        String key = method.toUpperCase() + "#" + uri;

        try {
            // 判断是否是 静态资源文件...
            if (isStatics(uri)) {
                webContent = WebContent.ael.getStaticsResourcesHandler().rander(webContent);
            } else {
                Integer automInt = getAutomInt(method, uri);
                if (null != automInt) {
                    Route route = routeMap.get(automInt);
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

    private boolean isStatics(String uri) {
        for (String resourcesHandler : WebContent.ael.getStaticsResourcesHandler().getResourcesHandlers()) {
            if (uri.startsWith(resourcesHandler)) {
                return true;
            }
        }
        return false;
    }

}
