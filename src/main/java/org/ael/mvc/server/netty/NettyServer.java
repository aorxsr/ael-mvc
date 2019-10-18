package org.ael.mvc.server.netty;

import cn.hutool.core.util.ClassUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.ael.mvc.Ael;
import org.ael.mvc.annotation.Configuration;
import org.ael.mvc.commons.ClassUtils;
import org.ael.mvc.commons.StringUtils;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.handler.init.InitHandler;
import org.ael.mvc.http.WebContent;
import org.ael.mvc.http.session.SessionClearHandler;
import org.ael.mvc.route.RouteHandler;
import org.ael.mvc.server.Server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
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
        // environment 初始化
        ael.getEnvironment().initConfig();

        String scanPackage = ael.getEnvironment().getString(EnvironmentConstant.SCAN_PACKAGE);
        if (StringUtils.isEmpty(scanPackage) && null != ael.getStartClass()) {
            scanPackage = ael.getStartClass().getPackage().getName();
        }
        if (StringUtils.isNotEmpty(scanPackage)) {
            Set<Class<?>> classes = ClassUtil.scanPackage(scanPackage);
            ael.addScanClass(RouteHandler.class);
            // 设置给ael
            ael.setScanClass(classes);
        }

        // 获取所有 @Configuration 类
        Class<Configuration> configuration = Configuration.class;
        Class<InitHandler> initHandler = InitHandler.class;
        ael.getScanClass()
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

    private boolean configHandler(Class<Configuration> configuration, Class<InitHandler> initHandlerClass, Class<?> aClass) {
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
    }

}
