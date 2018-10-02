package com.fourigin.argo.forms;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NettyServer {

    private int port;

    private String contextPath;

    public static void main(String... args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(NettyServer.class, args);

        Channel channel = context.getBean(Channel.class);

        channel.closeFuture().sync();
    }

    @Bean
    public ServerBootstrap serverBootstrap() {
        return new ServerBootstrap()
            .group(eventLoopGroup())
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new HttpServerInitializer(contextPath))
            .channel(NioServerSocketChannel.class);
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup eventLoopGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public Channel channel(@Autowired ServerBootstrap serverBootstrap) throws InterruptedException {
        return serverBootstrap.bind(port).sync().channel();
    }
    
    @Value("${server.port}")
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${server.netty.context-path}")
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
