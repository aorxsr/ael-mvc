package org.ael.mvc.http.body;

import io.netty.handler.codec.http.FullHttpResponse;

import java.io.InputStream;

/**
 * @Author: aorxsr
 * @Date: 2019/8/30 15:25
 */
public class StreamBody implements Body {

	private final InputStream inputStream;

	public StreamBody(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public FullHttpResponse body(BodyWrite write) {
		return write.onByteBuf(inputStream);
	}
}
