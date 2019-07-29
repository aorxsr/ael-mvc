package org.ael.mvc.http.body;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * @Author: aorxsr
 * @Date: 2019/7/25 14:30
 */
public class StringBody implements Body {

	private final byte[] bytes;

	public StringBody(final String content) {
		this.bytes = content.getBytes(StandardCharsets.UTF_8);
	}

	public static StringBody of(String content) {
		return new StringBody(content);
	}

	@Override
	public FullHttpResponse body(BodyWrite write) {
		return write.onByteBuf(Unpooled.copiedBuffer(bytes));
	}
}
