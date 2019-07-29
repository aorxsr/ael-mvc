package org.ael.mvc.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.EventExecutor;
import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.http.*;
import org.ael.mvc.http.body.Body;
import org.ael.mvc.http.body.BodyWrite;
import org.ael.mvc.http.body.ViewBody;
import org.ael.mvc.route.RouteHandler;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 16:56
 */
@ChannelHandler.Sharable
public class CustomHttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        EventExecutor executor = ctx.executor();
        CompletableFuture<HttpRequest> future = CompletableFuture.completedFuture(request);

        future.thenApplyAsync(req -> initRequest(req, ctx), executor)
                .thenApplyAsync(this::execute, executor)
                .thenApplyAsync(this::buildResponse, executor)
                .exceptionally(this::exectionHandler)
                .thenAcceptAsync(res -> writeResponse(ctx, future, res), ctx.channel().eventLoop());
    }

    private void writeResponse(ChannelHandlerContext ctx, CompletableFuture<HttpRequest> future, FullHttpResponse res) {
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
        future.complete(null);
    }

    private FullHttpResponse exectionHandler(Throwable throwable) {
        throwable.printStackTrace();
        return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(throwable.getMessage().getBytes()));
    }

    private WebContent initRequest(HttpRequest request, ChannelHandlerContext ctx) {
        request.initRequest(ctx.channel().remoteAddress().toString());
        return new WebContent(request, new HttpResponse(), ctx);
    }

    private WebContent execute(WebContent webContent) {
        RouteHandler routeHandler = WebContent.ael.getRouteHandler();

        return routeHandler.executeHandler(webContent);
    }

    private FullHttpResponse buildResponse(WebContent webContent) {
        Response response = webContent.getResponse();
        Request request = webContent.getRequest();
        Session session = request.getSession();
        if (null != session) {
            response.setCookie(new Cookie(HttpConstant.DEFAULT_SESSION_KEY, session.getId(), 0, null, null, false, true));
        }

        return response.getBody().body(new BodyWrite() {
            @Override
            public FullHttpResponse onView(ViewBody body) {
                return null;
            }

            @Override
            public FullHttpResponse onByteBuf(Object byteBuf) {
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

    private void appendResponseCookie(Set<io.netty.handler.codec.http.cookie.Cookie> cookieSet, FullHttpResponse response) {
        cookieSet.forEach(cookie -> response.headers().add(HttpConstant.SET_COOKIE,
                io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(cookie)));
    }

}
