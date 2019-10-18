package org.ael.mvc.container;

import java.io.InputStream;

public class ClassPathFileConstant {

    private static final Class clazz = ClassPathFileConstant.class;

    public static InputStream getClassPathFile(String classPath) {
        InputStream resourceAsStream = clazz.getClassLoader().getResourceAsStream(classPath);
        if (null == resourceAsStream) {
            resourceAsStream = clazz.getResourceAsStream(classPath);
        }
        return resourceAsStream;
    }

}
