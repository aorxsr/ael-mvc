package org.ael.mvc.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author aorxsr
 * @date 2019/10/19
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnhanceEnable {}
