package org.ael.route;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:44
 */
@Data
@Builder
public class Route {

    private String path;
    private String httpMethod;

    private Class<?> classType;

    private Object target;
    private Method method;

    private String contentType;

    private int routeType;

    private RouteFunctionHandler routeFunctionHandler;

}
