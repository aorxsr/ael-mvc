package org.ael.orm.annotation.dml;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Insert {
    String sql() default "";
}
