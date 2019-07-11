package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.initialization.ExternalValueResolverFactory;
import com.fourigin.argo.forms.prepopulation.PrePopulationValuesResolver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Set;

public class HttpServerInitializer extends ChannelInitializer<Channel> {

    private final String contextPath;
    private CustomerRepository customerRepository;
    private FormsStoreRepository formsStoreRepository;
    private FormDefinitionResolver formDefinitionResolver;
    private ExternalValueResolverFactory externalValueResolverFactory;
    private Set<PrePopulationValuesResolver> prePopulationValuesResolvers;
    private MessageSource messageSource;
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(HttpServerInitializer.class);

    public HttpServerInitializer(
        String contextPath,
        CustomerRepository customerRepository,
        FormsStoreRepository formsStoreRepository,
        FormDefinitionResolver formDefinitionResolver,
        ExternalValueResolverFactory externalValueResolverFactory,
        Set<PrePopulationValuesResolver> prePopulationValuesResolvers,
        MessageSource messageSource,
        ObjectMapper objectMapper
    ) {
        this.contextPath = contextPath;
        this.customerRepository = customerRepository;
        this.formsStoreRepository = formsStoreRepository;
        this.formDefinitionResolver = formDefinitionResolver;
        this.externalValueResolverFactory = externalValueResolverFactory;
        this.prePopulationValuesResolvers = prePopulationValuesResolvers;
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
            customerRepository,
                formDefinitionResolver,
            formsStoreRepository,
            externalValueResolverFactory,
            prePopulationValuesResolvers,
            messageSource,
            objectMapper
        ));
    }
}
