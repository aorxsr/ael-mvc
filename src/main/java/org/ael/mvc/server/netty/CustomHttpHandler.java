package org.ael.mvc.server.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.concurrent.EventExecutor;
import org.ael.mvc.http.HttpRequest;
import org.ael.mvc.http.HttpResponse;
import org.ael.mvc.http.WebContent;

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

	private FullHttpResponse buildResponse(WebContent webContent) {

		return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("OK".getBytes()));
	}

	private FullHttpResponse exectionHandler(Throwable throwable) {
		return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(throwable.getMessage().getBytes()));
	}

	private WebContent initRequest(HttpRequest request, ChannelHandlerContext ctx) {
		request.initRequest(ctx.channel().remoteAddress().toString());
		return new WebContent(request, new HttpResponse(), ctx);
	}

	private WebContent execute(WebContent webContent) {
		int i = 1 / 0;
		return webContent;
	}

}
