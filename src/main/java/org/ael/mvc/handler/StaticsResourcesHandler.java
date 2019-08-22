package org.ael.mvc.handler;

import lombok.Getter;
import org.ael.mvc.http.Request;
import org.ael.mvc.http.WebContent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 20:28
 */
public class StaticsResourcesHandler {

	@Getter
	private Map<String, String> resources = new LinkedHashMap<>(8);

	private Set<String> resourcesHandlers = new HashSet<>(8);

	private Map<String, String> MAPPING_CACHE = new HashMap<>(16);

	public WebContent rander(WebContent webContent) {
		// 去除？后面的, 然后转换为
		Request request = webContent.getRequest();
		String uri = request.getUri();

		// 匹配开始

		for (String resourcesHanler : resourcesHandlers) {
			if (uri.startsWith(resourcesHanler)) {
				uri =
				break;
			}
		}





		return webContent;
	}

	public void put

}
