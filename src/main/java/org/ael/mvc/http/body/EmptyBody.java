package org.ael.mvc.http.body;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 14:04
 */
public class EmptyBody implements Body {

	private final static EmptyBody EMPTY = new EmptyBody();

	public static EmptyBody of() {
		return EMPTY;
	}

	@Override
	public FullHttpResponse body(BodyWrite write) {
		return write.onByteBuf(Unpooled.buffer(0));
	}
}
