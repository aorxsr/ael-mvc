package org.ael.http;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.ael.Ael;
import org.ael.http.inter.Request;
import org.ael.http.inter.Response;

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
    @Setter
    private Response response;
    @Getter
    private ChannelHandlerContext ctx;

    public WebContent(Request request, Response response, ChannelHandlerContext ctx) {
        this.request = request;
        this.response = response;
        this.ctx = ctx;
    }

}
