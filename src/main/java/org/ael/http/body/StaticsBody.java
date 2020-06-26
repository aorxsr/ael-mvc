package org.ael.http.body;

import io.netty.handler.codec.http.FullHttpResponse;
import org.ael.template.give.ReadStaticResources;

import java.io.IOException;

public class StaticsBody implements Body {

    private final ReadStaticResources readStaticResources;

    public StaticsBody(ReadStaticResources readStaticResources) {
        this.readStaticResources = readStaticResources;
    }

    @Override
    public FullHttpResponse body(BodyWrite write) throws IOException {
        return write.onStatics(readStaticResources.getByteBuf());
    }

}
