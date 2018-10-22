package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

public class HttpServerInitializer extends ChannelInitializer<Channel> {

    private final String contextPath;
    private FormsStoreRepository formsStoreRepository;
    private FormDefinitionRepository formDefinitionRepository;
    private FormsProcessingDispatcher formsProcessingDispatcher;
    private MessageSource messageSource;
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(HttpServerInitializer.class);

    public HttpServerInitializer(
        String contextPath,
        FormsStoreRepository formsStoreRepository,
        FormDefinitionRepository formDefinitionRepository,
        FormsProcessingDispatcher formsProcessingDispatcher,
        MessageSource messageSource, ObjectMapper objectMapper
    ) {
        this.contextPath = contextPath;
        this.formsStoreRepository = formsStoreRepository;
        this.formDefinitionRepository = formDefinitionRepository;
        this.formsProcessingDispatcher = formsProcessingDispatcher;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;

        if (logger.isInfoEnabled()) logger.info("Initializing with contextPath {}", contextPath);
    }

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(new HttpServerHandler(
            contextPath,
            formDefinitionRepository,
            formsStoreRepository,
            formsProcessingDispatcher,
            messageSource,
            objectMapper
        ));
    }
}
