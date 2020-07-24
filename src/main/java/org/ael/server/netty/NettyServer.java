package org.ael.server.netty;

import cn.hutool.core.util.ClassUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.ael.Ael;
import org.ael.constant.EnvironmentConstant;
import org.ael.http.WebContent;
import org.ael.http.session.SessionClearHandler;
import org.ael.ioc.core.util.ClsUtil;
import org.ael.server.Server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
@Slf4j
public class NettyServer implements Server {

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup boss;
    private EventLoopGroup work;

    private EventLoop scheduleEventLoop;

    private ChannelFuture future;

    private Ael ael;

    @Deprecated
    public static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    @Override
    public void start(Ael ael) {
        this.ael = ael;
        init();
        WebContent.setAel(ael);
        ael.getAelTemplate().init(ael);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop()));
        startServer();
    }

    private void startServer() {
        int port = ael.getEnvironment().getInt("server.port", 7788);
        new Thread(() -> {
            try {
                future = serverBootstrap.channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new InitialHandler())
                        .bind(port)
                        .sync()
                        .channel()
                        .closeFuture()
                        .sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        log.info("open in browser http://127.0.0.1:" + port);
    }

    private void init() {

        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();


        scheduleEventLoop = new DefaultEventLoop();
        scheduleEventLoop.scheduleAtFixedRate(new SessionClearHandler(ael.getSessionHandler().getSessionManager()), 1000, 1000, TimeUnit.MILLISECONDS);

        serverBootstrap = new ServerBootstrap();
        // 初始化配置
        serverBootstrap.group(work, boss);
        // 扫描包下面所有的类
        List<String> list = this.ael.getEnvironment().getList(EnvironmentConstant.SCAN_PACKAGE, new ArrayList<String>());

        Set<Class<?>> scanClazz = new HashSet<>(16);
        list.forEach(bap -> ClsUtil.getClasses(bap).forEach(cls -> scanClazz.add(cls)));
        this.ael.setScanClass(scanClazz);

        // routeHandler init
        ael.getRouteHandler().init();
        ael.getRouteHandler().initUrlHandler();

        // function route register
        ael.getFunctionRouteHandler().registerFunctionRoute(ael.getRouteHandler());

        // ioc init
        ael.getIocPlugin().initIoc(ael);

        // aop init
        ael.getAopPlugin().init(ael);

        // hook init
        ael.getHookHandler().initHook(ael.getScanClass());

        // dataSource init
        ael.getSqlFactoryBuilder().init(ael);
    }

    @Override
    public void stop() {
        if (!work.isShutdown()) {
            work.shutdownGracefully();
        }
        if (!boss.isShutdown()) {
            boss.shutdownGracefully();
        }
        // 关闭线程池
        executorService.shutdown();
    }

}
