package org.ael;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;
import org.ael.route.exception.NoMappingException;
import org.ael.route.function.FunctionRouteHandler;
import org.ael.server.netty.exception.ExcInfo;
import org.ael.server.netty.exception.ExecuteException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class TEst {

    public static void main(String[] args) {

        Ael ael = new Ael();

        FunctionRouteHandler functionRouteHandler = ael.getFunctionRouteHandler();

        functionRouteHandler.getRoute("/", webContent -> webContent.getResponse().json(JSONObject.toJSONString(Money.builder().zjq(100).zqq(50).build())));

        ExecuteException executeException = ael.getExecuteException();
        executeException.addException(NoMappingException.class, new ExcInfo("org.ael.exception.Exceptions", "noMapping"));

        ael.start();

    }

    @Builder
    @Data
    static class Money {
        private int zjq;
        private int zqq;
    }

    @Test
    public void test() throws NoSuchMethodException {
        Method method = TEst.class.getMethod("main", String[].class);

        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] types = method.getGenericParameterTypes();

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            System.out.println(parameters[i].getName());
        }

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];

            System.out.println(type.getTypeName());

        }


    }

    @Test
    public void jsonTest() {
        String s = "asdf";

        System.out.println(JSONObject.toJSONString(""));


        String name = "a.a.a.aaa()";

        System.out.println(name.substring(0, name.lastIndexOf('.')));

        System.out.println(name.substring(name.lastIndexOf('.') + 1));

    }

    @Test
    public void T() {
        String a = "org.ael.HelloController$$EnhancerByCGLIB$$b785f121";

        System.out.println(a.substring(0, a.indexOf('$')));

    }

}
