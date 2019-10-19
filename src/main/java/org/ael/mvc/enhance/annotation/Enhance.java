package org.ael.mvc.enhance.annotation;

import org.ael.mvc.constant.HttpMethod;

import java.lang.annotation.*;

/**
 * 增强的注解
 *
 * @author aorxsr
 * @date 2019/10/19
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Enhance {

    String url() default "";

    /**
     * enhance name
     *
     * @return
     */
    String name() default "";

    /**
     * 顺序
     *
     * @return
     */
    int order() default 0;

    /**
     * 拦截请求类型
     */
    HttpMethod httpMethod() default HttpMethod.ALL;

}
