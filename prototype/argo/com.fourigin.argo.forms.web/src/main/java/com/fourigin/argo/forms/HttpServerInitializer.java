package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerInitializer extends ChannelInitializer<Channel> {

    private final String contextPath;
    private FormsStoreRepository formsStoreRepository;
    private FormDefinitionRepository formDefinitionRepository;
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(HttpServerInitializer.class);

    public HttpServerInitializer(
        String contextPath,
        FormsStoreRepository formsStoreRepository,
        FormDefinitionRepository formDefinitionRepository,
        ObjectMapper objectMapper
    ) {
        this.contextPath = contextPath;
        this.formsStoreRepository = formsStoreRepository;
        this.formDefinitionRepository = formDefinitionRepository;
        this.objectMapper = objectMapper;

        if (logger.isInfoEnabled()) logger.info("Initializing with contextPath {}", contextPath);
    }

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(new HttpServerHandler(contextPath, formDefinitionRepository, formsStoreRepository, objectMapper));
    }
}
