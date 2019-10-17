package org.ael.mvc.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author: aorxsr
 * @Date: 2019/7/18 18:24
 *
 * 初始化Channel
 */
public class InitialHandler extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		// SSL
//		pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpServerExpectContinueHandler());
		// 聚合Http请求
		pipeline.addLast(new ConversionsRequest());
		// 但是不占用过多的内存，防止发生java内存溢出错误
		pipeline.addLast(new ChunkedWriteHandler());
		// 自己的业务
		pipeline.addLast(new CustomHttpHandler());
	}
}
