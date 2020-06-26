package org.ael.handler;

import lombok.Getter;
import org.ael.commons.StreamUtils;
import org.ael.exception.ViewNotFoundException;
import org.ael.http.body.StaticsBody;
import org.ael.http.inter.Request;
import org.ael.http.WebContent;
import org.ael.template.AelTemplate;
import org.ael.template.ModelAndView;
import org.ael.template.give.ReadStaticResources;

import java.io.InputStream;
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

    public WebContent rander(WebContent webContent) {
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

        // 直接读
        InputStream resourceAsStream = StreamUtils.getClassPathFile(uri);
        if (null == resourceAsStream) {
            throw new ViewNotFoundException("not found ... ");
        }

        webContent.getResponse().write(new StaticsBody(new ReadStaticResources(resourceAsStream)));

        return webContent;
    }


}
