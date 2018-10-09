package com.fourigin.argo.forms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.fourigin.argo.config")
public class NettyServer {

    private static final String APP_NAME = "argo-forms";

    private int port;

    private String contextPath;

    public static void main(String... args) throws Exception {
        SpringApplication app = new SpringApplication(NettyServer.class);
        app.addListeners(
            new ApplicationPidFileWriter(APP_NAME + ".pid")
        );

        ConfigurableApplicationContext context = app.run(args);

        Channel channel = context.getBean(Channel.class);

        channel.closeFuture().sync();
    }

    @Bean
    public ServerBootstrap serverBootstrap(
        @Autowired FormsStoreRepository formsStoreRepository,
        @Autowired FormDefinitionRepository formDefinitionRepository
    ) {
        return new ServerBootstrap()
            .group(eventLoopGroup())
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new HttpServerInitializer(
                contextPath,
                formsStoreRepository,
                formDefinitionRepository,
                objectMapper()
            ))
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

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return objectMapper;
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
