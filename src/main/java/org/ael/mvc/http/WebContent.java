package org.ael.mvc.http;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 19:53
 */
public class WebContent {

	private Request request;
	private Response response;

	private ChannelHandlerContext ctx;

	public WebContent(Request request, Response response, ChannelHandlerContext ctx) {
		this.request = request;
		this.response = response;
		this.ctx = ctx;
	}

}
