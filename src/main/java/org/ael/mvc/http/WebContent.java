package org.ael.mvc.http;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.ael.mvc.Ael;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 19:53
 */
public class WebContent {

    @Setter
    public static Ael ael;

    @Getter
    private Request request;
    @Getter
    private Response response;
    @Getter
    private ChannelHandlerContext ctx;

    public WebContent(Request request, Response response, ChannelHandlerContext ctx) {
        this.request = request;
        this.response = response;
        this.ctx = ctx;
    }

}
