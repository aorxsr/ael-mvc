package org.ael.orm.annotation.dml;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Query {
    String sql() default "";
}
