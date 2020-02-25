package org.ael.route.function;

import lombok.Data;
import org.ael.Ael;
import org.ael.constant.RouteTypeConstant;
import org.ael.route.Route;
import org.ael.route.RouteHandler;

import java.util.HashSet;
import java.util.Set;

import static org.ael.constant.HttpMethodConstant.GET_UPPER;
import static org.ael.constant.HttpMethodConstant.POST_UPPER;

/**
 * @author aorxsr
 * @date 2020/2/25
 */
@Data
public class FunctionRouteHandler {

    private Set<Route> routeSet = new HashSet<>(16);

    public FunctionRouteHandler addRoute(String requestType, String url, RouteFunctionHandler handler) {
        routeSet.add(Route.builder()
                .httpMethod(requestType)
                .classType(handler.getClass())
                .path(url)
                .routeFunctionHandler(handler)
                .routeType(RouteTypeConstant.ROUTE_TYPE_FUNCTION)
                .build());
        return this;
    }

    public FunctionRouteHandler getRoute(String url, RouteFunctionHandler handler) {
        routeSet.add(Route.builder()
                .httpMethod(GET_UPPER)
                .classType(handler.getClass())
                .path(url)
                .routeFunctionHandler(handler)
                .routeType(RouteTypeConstant.ROUTE_TYPE_FUNCTION)
                .build());
        return this;
    }

    public FunctionRouteHandler postRoute(String url, RouteFunctionHandler handler) {
        routeSet.add(Route.builder()
                .httpMethod(POST_UPPER)
                .classType(handler.getClass())
                .path(url)
                .routeFunctionHandler(handler)
                .routeType(RouteTypeConstant.ROUTE_TYPE_FUNCTION)
                .build());
        return this;
    }

    public void registerFunctionRoute(RouteHandler routeHandler) {
        routeSet.forEach(route -> routeHandler.addFunctionRoute(route.getHttpMethod(), route.getPath(), route));
    }

}
