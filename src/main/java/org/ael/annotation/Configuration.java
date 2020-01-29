package org.ael.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {
    /**
     * 初始化
     * @return
     */
    int order() default 0;

}
