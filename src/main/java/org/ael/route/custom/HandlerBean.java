package org.ael.route.custom;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Data
public class HandlerBean {
    private String url;
    private Object object;
    private Class<?> aClass;
    private Method method;

    private Pattern pattern;

}
