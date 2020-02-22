package org.ael.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.ael.constant.HttpConstant;
import org.ael.http.HttpRequest;
import org.ael.http.HttpResponse;
import org.ael.http.WebContent;
import org.ael.http.body.SimpleBodyWrite;
import org.ael.http.inter.Request;
import org.ael.plugin.aop.AopPlugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 16:56
 */
@Slf4j
@ChannelHandler.Sharable
public class CustomHttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        FullHttpResponse response;
        try {
            WebContent execute = execute(request, ctx);
            response = execute.getResponse().getBody().body(new SimpleBodyWrite(execute));
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.getCause().printStackTrace(printWriter);
            response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(writer.toString().getBytes()));
        }

        AopPlugin.WEB_CONTENT_THREAD_LOCAL.remove();

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private WebContent execute(Request request, ChannelHandlerContext ctx) {
        request.initRequest(ctx.channel().remoteAddress().toString());
        return WebContent.ael.getRouteHandler().executeHandler(new WebContent(request, new HttpResponse(), ctx));
    }

}
