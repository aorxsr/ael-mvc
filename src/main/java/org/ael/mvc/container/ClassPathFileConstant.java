package org.ael.mvc.container;

import java.io.InputStream;

public class ClassPathFileConstant {

    public static InputStream getClassPathFile(String classPath) {
        InputStream resourceAsStream = ClassPathFileConstant.class.getClassLoader().getResourceAsStream(classPath);
        if (null == resourceAsStream) {
            resourceAsStream = ClassPathFileConstant.class.getResourceAsStream(classPath);
        }
        return resourceAsStream;
    }

}
