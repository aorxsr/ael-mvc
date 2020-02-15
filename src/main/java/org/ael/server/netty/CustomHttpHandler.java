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
        /*NettyServer.executorService.execute(() -> CompletableFuture.completedFuture(request)
                .thenApplyAsync(httpRequest -> initRequest(httpRequest, ctx))
                .thenApplyAsync(this::execute)
                .thenApplyAsync(this::buildResponse)
                .exceptionally(this::exceptionally)
                .thenAcceptAsync(fullHttpResponse -> writeResponse(ctx, fullHttpResponse), ctx.channel().eventLoop()));*/
        CompletableFuture.completedFuture(request)
                .thenApplyAsync(httpRequest -> initRequest(httpRequest, ctx))
                .thenApplyAsync(this::execute)
                .thenApplyAsync(this::buildResponse)
                .exceptionally(this::exceptionally)
                .thenAcceptAsync(fullHttpResponse -> writeResponse(ctx, fullHttpResponse), ctx.channel().eventLoop());
    }

    public FullHttpResponse exceptionally(Throwable cause) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        cause.printStackTrace(printWriter);
        return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(writer.toString().getBytes()));
    }

    private void writeResponse(ChannelHandlerContext ctx, FullHttpResponse res) {
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
    }

    private WebContent initRequest(HttpRequest request, ChannelHandlerContext ctx) {
        request.initRequest(ctx.channel().remoteAddress().toString());
        return new WebContent(request, new HttpResponse(), ctx);
    }

    private WebContent execute(WebContent webContent) {
        return WebContent.ael.getRouteHandler().executeHandler(webContent);
    }

    private FullHttpResponse buildResponse(WebContent webContent) {

        try {
            return webContent.getResponse().getBody().body(new SimpleBodyWrite(webContent));
        } catch (IOException e) {
            webContent.getResponse().setContentType("text");
            webContent.getResponse().text(e.getMessage());

            ByteBuf buffer = Unpooled.buffer();
            buffer.readBytes(e.getMessage().getBytes());

            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(400), buffer);
            defaultFullHttpResponse.headers().set(HttpConstant.DATE, new Date());
            defaultFullHttpResponse.headers().add("Content-Type", "text");
            return defaultFullHttpResponse;
        }
    }

}
