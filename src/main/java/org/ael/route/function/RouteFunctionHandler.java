package org.ael.route.function;

import org.ael.http.WebContent;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 11:51
 */
@FunctionalInterface
public interface RouteFunctionHandler {

	/**
	 * 
	 * @param webContent
	 */
	void handler(WebContent webContent);

}
