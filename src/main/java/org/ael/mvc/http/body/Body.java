package org.ael.mvc.http.body;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 13:54
 */
public interface Body {

	FullHttpResponse body(BodyWrite write);

}
