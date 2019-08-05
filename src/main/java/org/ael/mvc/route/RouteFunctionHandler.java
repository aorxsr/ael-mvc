package org.ael.mvc.route;

import org.ael.mvc.http.WebContent;

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
