package org.ael.mvc.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.ael.mvc.Ael;
import org.ael.mvc.server.Server;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
public class NettyServer implements Server {

	private ServerBootstrap serverBootstrap;

	private EventLoopGroup boss;
	private EventLoopGroup work;

	private Ael ael;

	@Override
	public void start(Ael ael) throws InterruptedException {
		init();

		startServer();
	}

	/**
	 * 启动
	 */
	private void startServer() throws InterruptedException {
		ChannelFuture sync = serverBootstrap.channel(NioServerSocketChannel.class)
				.childHandler(new InitialHandler())
				.bind(7788)
				.sync();

		System.out.println("open in browser http://127.0.0.1:7788 ");

		sync.channel().closeFuture().sync();
	}

	private void init() {
		boss = new NioEventLoopGroup();
		work = new NioEventLoopGroup();

		serverBootstrap = new ServerBootstrap();
		// 初始化配置
		serverBootstrap.group(work, boss);
	}

	@Override
	public void stop() {

	}
}
