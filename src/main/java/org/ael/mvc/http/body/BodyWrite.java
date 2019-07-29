package org.ael.mvc.http.body;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @Author: aorxsr
 * @Date: 2019/7/25 18:48
 */
public interface BodyWrite {

	FullHttpResponse onView(ViewBody body);

	FullHttpResponse onByteBuf(Object byteBuf);

	FullHttpResponse onByteBuf(ByteBuf byteBuf);

}
