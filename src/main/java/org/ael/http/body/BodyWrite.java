package org.ael.http.body;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.IOException;

/**
 * @Author: aorxsr
 * @Date: 2019/7/25 18:48
 */
public interface BodyWrite {

    FullHttpResponse onView(ViewBody body) throws IOException;

    FullHttpResponse onByteBuf(Object byteBuf);

    FullHttpResponse onByteBuf(ByteBuf byteBuf);

    FullHttpResponse onStatics(ByteBuf byteBuf);

}
