package org.ael.plugin.aop.annotation;

import java.lang.annotation.*;

/**
 * @author aorxsr
 * @date 2020/2/19
 * 写在类上就是这个类里的方法全部都拦截,如果是在方法上就是单个拦截
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface SurroundEnhance {
    String enhanceMethodName() default "";
}
