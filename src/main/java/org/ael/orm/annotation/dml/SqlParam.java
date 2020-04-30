package org.ael.orm.annotation.dml;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface SqlParam {
    String value() default "";
}
