package org.ael.mvc.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.ael.mvc.Ael;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.http.WebContent;
import org.ael.mvc.http.session.SessionClearHandler;
import org.ael.mvc.server.Server;

import java.util.concurrent.TimeUnit;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
public class NettyServer implements Server {

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup boss;
    private EventLoopGroup work;

    private EventLoop scheduleEventLoop;

    private ChannelFuture future;

    private Ael ael;

    @Override
    public void start(Ael ael) {
        this.ael = ael;
        init();
        //
        ael.getContainer().initContainer();

        WebContent.setAel(ael);

        ael.getAelTemplate().init(ael);

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop()));
            startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws InterruptedException {
        future = serverBootstrap.channel(NioServerSocketChannel.class)
                .childHandler(new InitialHandler())
                .bind(7788)
                .sync();

        System.out.println("open in browser http://127.0.0.1:7788 ");

        future.channel().closeFuture().sync();
    }

    private void init() {
        ael.getEnvironment().initConfig();

        ael.getRouteHandler().initRouteHandler(ael);

        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();

        scheduleEventLoop = new DefaultEventLoop();
        scheduleEventLoop.scheduleAtFixedRate(new SessionClearHandler(ael.getSessionHandler().getSessionManager()), 1000, 1000, TimeUnit.MILLISECONDS);

        serverBootstrap = new ServerBootstrap();
        // 初始化配置
        serverBootstrap.group(work, boss);
    }

    @Override
    public void stop() {
        if (!work.isShutdown()) {
            work.shutdownGracefully();
        }
        if (!boss.isShutdown()) {
            boss.shutdownGracefully();
        }
    }

}
