package org.ael.route.custom;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface UrlHandler {
    String value()[] default "";
}
