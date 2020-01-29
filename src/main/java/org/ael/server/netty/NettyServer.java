package org.ael.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.ael.commons.ClassUtils;
import org.ael.handler.init.AbstractInitHandler;
import org.ael.Ael;
import org.ael.annotation.Configuration;
import org.ael.constant.EnvironmentConstant;
import org.ael.http.WebContent;
import org.ael.http.session.SessionClearHandler;
import org.ael.server.Server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    public static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    @Override
    public void start(Ael ael) {
        this.ael = ael;
        init();
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
        new Thread(() -> {
            try {
                future = serverBootstrap.channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new InitialHandler())
                        .bind(7788)
                        .sync()
                        .channel()
                        .closeFuture()
                        .sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("open in browser http://127.0.0.1:7788 ");
    }

    private void init() {
        // environment 初始化
        ael.getEnvironment().initConfig();

        List<String> scanPackage = ael.getEnvironment().getList(EnvironmentConstant.SCAN_PACKAGE, new ArrayList());
        scanPackage.add("org.ael");
        ael.getEnvironment().put(EnvironmentConstant.SCAN_PACKAGE, scanPackage);

        // 获取所有 @Configuration 类
        Class<Configuration> configuration = Configuration.class;
        Class<AbstractInitHandler> initHandler = AbstractInitHandler.class;
        ClassUtils.getClasss(scanPackage, configuration)
                .stream()
                .filter(aClass -> configHandler(configuration, initHandler, aClass))
                .sorted((aClass, bClass) -> {
                    Configuration aConfiguration = aClass.getAnnotation(configuration);
                    Configuration bConfiguration = bClass.getAnnotation(configuration);
                    int aOrder = aConfiguration.order();
                    int bOrder = bConfiguration.order();
                    if (aOrder > bOrder) {
                        return 1;
                    } else if (aOrder == bOrder) {
                        return 0;
                    } else {
                        return -1;
                    }
                }).forEach(aClass -> {
            try {
                Object instance = aClass.newInstance();
                Method init = aClass.getMethod("init", Ael.class);
                init.invoke(instance, ael);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });


        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();

        scheduleEventLoop = new DefaultEventLoop();
        scheduleEventLoop.scheduleAtFixedRate(new SessionClearHandler(ael.getSessionHandler().getSessionManager()), 1000, 1000, TimeUnit.MILLISECONDS);

        serverBootstrap = new ServerBootstrap();
        // 初始化配置
        serverBootstrap.group(work, boss);
    }

    private boolean configHandler(Class<Configuration> configuration, Class<AbstractInitHandler> initHandlerClass, Class<?> aClass) {
        if (aClass.isAnnotationPresent(configuration)) {
            try {
                aClass.asSubclass(initHandlerClass);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
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
