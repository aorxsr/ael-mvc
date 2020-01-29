package org.ael.http.body;

import io.netty.handler.codec.http.FullHttpResponse;

import java.io.IOException;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 13:54
 */
public interface Body {

    FullHttpResponse body(BodyWrite write) throws IOException;

}
