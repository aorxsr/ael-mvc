package org.ael.exception;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.ael.http.HttpRequest;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class Exceptions {

    public FullHttpResponse noMapping(Exception e, HttpRequest request) {

        return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(("No Mapping : " + request.getUri()).getBytes()));
    }

}
