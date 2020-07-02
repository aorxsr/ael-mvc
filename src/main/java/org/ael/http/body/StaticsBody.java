package org.ael.http.body;

import io.netty.handler.codec.http.FullHttpResponse;
import org.ael.constant.ContentType;
import org.ael.http.WebContent;
import org.ael.http.inter.Request;
import org.ael.plugin.aop.AopPlugin;
import org.ael.template.give.ReadStaticResources;

import java.io.IOException;

public class StaticsBody implements Body {

    private final ReadStaticResources readStaticResources;

    public StaticsBody(ReadStaticResources readStaticResources) {
        this.readStaticResources = readStaticResources;
    }

    @Override
    public FullHttpResponse body(BodyWrite write) throws IOException {
        WebContent webContent = AopPlugin.WEB_CONTENT_THREAD_LOCAL.get();
        Request request = webContent.getRequest();
        String uri = request.getUri();
        String suffix = uri.substring(uri.lastIndexOf(".") + 1);
        webContent.getResponse().setContentType(ContentType.get(suffix));
        return write.onStatics(readStaticResources.getByteBuf());
    }

}
