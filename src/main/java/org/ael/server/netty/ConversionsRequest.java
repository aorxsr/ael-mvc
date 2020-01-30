package org.ael.server.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import org.ael.http.HttpRequest;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @Author: aorxsr
 * @Date: 2019/7/18 18:25
 */
public class ConversionsRequest extends SimpleChannelInboundHandler<HttpObject> {
	private HttpRequest httpRequest;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
			httpRequest = new HttpRequest();
			httpRequest.setNettyRequest((io.netty.handler.codec.http.HttpRequest) msg);
			return;
		}
		if (null != httpRequest && msg instanceof HttpContent) {
			httpRequest.appendHttpContent((HttpContent) msg);
		}
		if (msg instanceof LastHttpContent) {
			if (null != httpRequest) {
				ctx.fireChannelRead(httpRequest);
			} else {
				ctx.fireChannelRead(msg);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(cause.getMessage().getBytes()));
		ctx.write(response).addListener(ChannelFutureListener.CLOSE);
	}

}
