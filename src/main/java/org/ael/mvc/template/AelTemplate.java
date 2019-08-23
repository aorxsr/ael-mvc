package org.ael.mvc.template;

import org.ael.mvc.Ael;
import org.ael.mvc.exception.ViewNotFoundException;
import org.ael.mvc.http.WebContent;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 19:49
 */
public interface AelTemplate {

	/**
	 * 模板实现接口
	 *
	 * @param modelAndView
	 * @return
	 */
	WebContent render(ModelAndView modelAndView, WebContent webContent) throws ViewNotFoundException;

	/**
	 * 初始化
	 *
	 * @param ael
	 */
	void init(Ael ael);

	String readFileContext(String view) throws ViewNotFoundException;

}
