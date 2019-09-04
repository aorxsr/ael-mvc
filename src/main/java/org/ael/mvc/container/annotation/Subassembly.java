package org.ael.mvc.container.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: aorxsr
 * @Date: 2019/9/2 15:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subassembly {

	/**
	 * 注释
	 *
	 * @return
	 */
	String description() default "";

}
