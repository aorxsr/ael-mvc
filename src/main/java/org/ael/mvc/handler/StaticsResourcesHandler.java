package org.ael.mvc.handler;

import lombok.Getter;
import org.ael.mvc.constant.ContentType;
import org.ael.mvc.exception.ViewNotFoundException;
import org.ael.mvc.http.Request;
import org.ael.mvc.http.WebContent;
import org.ael.mvc.template.AelTemplate;
import org.ael.mvc.template.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 20:28
 */
public class StaticsResourcesHandler {

	@Getter
	private Map<String, String> resources = new LinkedHashMap<>(8);

	@Getter
	private Set<String> resourcesHandlers = new HashSet<>(8);

	private Map<String, String> MAPPING_CACHE = new HashMap<>(16);

	public WebContent rander(WebContent webContent) throws ViewNotFoundException {
		// 去除？后面的, 然后转换为
		Request request = webContent.getRequest();
		String uri = request.getUri();

		// 匹配开始

		for (String resourcesHanler : resourcesHandlers) {
			if (uri.startsWith(resourcesHanler)) {
				if (resources.containsKey(resourcesHanler)) {
					uri = uri.replace(resourcesHanler, resources.get(resourcesHanler));
				}
				break;
			}
		}

		// 判断结尾是否是.html 或者 .
		if (uri.contains("?")) {
			uri = uri.substring(0, uri.indexOf("?"));
		}

		if (uri.contains(".")) {
			String suffix = uri.substring(uri.lastIndexOf(".") + 1);
			webContent.getResponse().setContentType(ContentType.get(suffix));
		}

		AelTemplate aelTemplate = WebContent.ael.getAelTemplate();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setView(uri);
		webContent = aelTemplate.render(modelAndView, webContent);
		return webContent;
	}


}
