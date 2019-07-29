package org.ael.mvc.route;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:44
 */
@Data
public class Route {

	private Object object;
	private Method method;

}
