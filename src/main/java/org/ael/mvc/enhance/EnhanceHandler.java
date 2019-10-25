package org.ael.mvc.enhance;

import org.ael.mvc.commons.StringUtils;
import org.ael.mvc.http.Request;
import org.ael.mvc.http.WebContent;

import java.util.Optional;

/**
 * @author aorxsr
 * @date 2019/10/19
 */
public class EnhanceHandler {

    private EnhanceBuilder enhanceBuilder;

    public EnhanceHandler(EnhanceBuilder enhanceBuilder) {
        this.enhanceBuilder = enhanceBuilder;
    }

    public EnhanceInfo executeEnhanceHandler(WebContent webContent) {
        Request request = webContent.getRequest();
        String uri = request.getUri();

        Optional<EnhanceInfo> firstEnhanceInfo = enhanceBuilder
                .enhanceInfos
                .stream()
                .filter(enhanceInfo -> enhanceInfo.getPattern().matcher(uri).matches())
                .findFirst();

        if (firstEnhanceInfo.isPresent()) {
            return firstEnhanceInfo.get();
        } else {
            return null;
        }
    }

}
