package com.fourigin.argo.forms;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

    private static final int HTTP_PORT = 8090;

    public static void main(String... args) throws Exception {

        Cache<Long, String> dataCache = CacheBuilder.newBuilder()
            .maximumSize(60)
            .build();

        dataCache.put(1L, "One");
        dataCache.put(2L, "Two");
        dataCache.put(3L, "Three");

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap httpBoostrap = new ServerBootstrap()
                .group(eventLoopGroup)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new HttpServerInitializer(dataCache))
                .channel(NioServerSocketChannel.class);


            Channel httpChannel = httpBoostrap.bind(HTTP_PORT).sync().channel();

            httpChannel.closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }

}
