package org.ael.template;

import io.netty.handler.codec.http.FullHttpResponse;
import org.ael.Ael;
import org.ael.exception.ViewNotFoundException;
import org.ael.http.WebContent;
import org.ael.template.give.ReadStaticResources;

import java.io.IOException;

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
	 * 模板实现接口
	 *
	 * @param modelAndView
	 * @return
	 */
	FullHttpResponse renderResponse(ModelAndView modelAndView, WebContent webContent) throws ViewNotFoundException;

	/**
	 * 初始化
	 *
	 * @param ael
	 */
	void init(Ael ael);

	ReadStaticResources readFileContext(String view) throws ViewNotFoundException, IOException;

}
