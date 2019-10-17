package org.ael.mvc.http.body;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

public class ByteBufBody implements Body {

    private ByteBuf byteBuf;

    public ByteBufBody(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public FullHttpResponse body(BodyWrite write) {
        return write.onByteBuf(byteBuf);
    }

}
