package org.ael;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class TEst {

    public static void main(String[] args) {

        Ael ael = new Ael();

        ael.get("/a", webContent -> {
            webContent.getResponse().text("F大大");
        })
                .start(TEst.class);

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

}
