package org.ael.mvc.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.exception.ViewNotFoundException;
import org.ael.mvc.http.*;
import org.ael.mvc.http.body.BodyWrite;
import org.ael.mvc.http.body.ViewBody;

import java.util.Date;
import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 16:56
 */
@ChannelHandler.Sharable
public class CustomHttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        try {
            writeResponse(ctx, buildResponse(execute(initRequest(request, ctx))));
        } catch (Exception e) {
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(e.getMessage().getBytes()));
            writeResponse(ctx, defaultFullHttpResponse);
        }
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
        Response response = webContent.getResponse();
        Request request = webContent.getRequest();
        Session session = request.getSession();
        if (null != session) {
            // 获取Cookie
            Cookie cookie = request.getCookie(WebContent.ael.getEnvironment().getString(EnvironmentConstant.SESSION_KEY, EnvironmentConstant.DEFAULT_SESSION_KEY));
            if (null == cookie) {
                response.setCookie(new DefaultCookie(HttpConstant.DEFAULT_SESSION_KEY, session.getId()));
            } else {
                response.setCookie(cookie);
            }
        }

        return response.getBody().body(new BodyWrite() {
            @Override
            public FullHttpResponse onView(ViewBody body) {
                // 读取文件
                try {
                    String context = WebContent.ael.getAelTemplate().readFileContext(body.getUrl());
                    DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus()), Unpooled.copiedBuffer(context.getBytes()));
                    appendResponseCookie(response.getNettyCookies(), defaultFullHttpResponse);
                    response.setContentType(HttpConstant.TEXT_HTML);
                    response.getHeaders().forEach((k, v) -> defaultFullHttpResponse.headers().set(k, v));
                    defaultFullHttpResponse.headers().set(HttpConstant.DATE, new Date());
                    return defaultFullHttpResponse;
                } catch (ViewNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public FullHttpResponse onByteBuf(Object byteBuf) {
                DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus()));
                response.getHeaders().forEach((k, v) -> defaultFullHttpResponse.headers().set(k, v));
                ChannelHandlerContext ctx = webContent.getCtx();
                ctx.write(defaultFullHttpResponse, ctx.voidPromise());
                ChannelFuture future = ctx.writeAndFlush(byteBuf);
                if (!request.isKeepAlive()) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
                return null;
            }

            @Override
            public FullHttpResponse onByteBuf(ByteBuf byteBuf) {
                DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus()), byteBuf);
                appendResponseCookie(response.getNettyCookies(), defaultFullHttpResponse);
                response.getHeaders().forEach((k, v) -> defaultFullHttpResponse.headers().set(k, v));
                defaultFullHttpResponse.headers().set(HttpConstant.DATE, new Date());
                return defaultFullHttpResponse;
            }
        });
    }

    private void appendResponseCookie(Set<Cookie> cookieSet, FullHttpResponse response) {
        cookieSet.forEach(cookie -> response.headers().add(HttpConstant.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie)));
    }

}
