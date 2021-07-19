package org.ael.server.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.ael.Ael;
import org.ael.http.HttpRequest;
import org.ael.http.HttpResponse;
import org.ael.http.WebContent;
import org.ael.http.body.SimpleBodyWrite;
import org.ael.http.inter.Request;
import org.ael.plugin.aop.AopPlugin;
import org.ael.route.exception.NoMappingException;
import org.ael.route.exception.NoRouteTypeException;
import org.ael.server.netty.exception.ExecuteException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

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
        FullHttpResponse response = null;
        try {
            WebContent execute = execute(request, ctx);
            response = execute.getResponse().getBody().body(new SimpleBodyWrite(execute));
        } catch (Exception e) {
            // 这里调用相应的方法， 否则就执行下面的东西
            ExecuteException executeException = WebContent.ael.getExecuteException();
            if (executeException.existenceExceptionType(e)) {
                response = executeException.executeException(e, request);
            } else {
                StringWriter writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                Throwable cause = e.getCause();
                if (null == cause) {
                    e.printStackTrace(printWriter);
                } else {
                    e.getCause().printStackTrace(printWriter);
                }
                response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(writer.toString().getBytes("UTF-8")));
            }
            e.printStackTrace();
        }
        AopPlugin.WEB_CONTENT_THREAD_LOCAL.remove();

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private WebContent execute(Request request, ChannelHandlerContext ctx) throws IllegalAccessException, IOException, InvocationTargetException, NoMappingException, NoRouteTypeException {
        request.initRequest(ctx.channel().remoteAddress().toString());
        return WebContent.ael.getRouteHandler().executeHandler(new WebContent(request, new HttpResponse(), ctx));
    }

}
