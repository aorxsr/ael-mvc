package org.ael.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author: aorxsr
 * @Date: 2019/7/31 20:20
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
}
