package com.fourigin.argo.forms;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.customer.payment.mapping.PaymentModule;
import com.fourigin.argo.forms.formatter.mapping.DataFormatterModule;
import com.fourigin.argo.forms.initialization.CustomerExternalValueResolver;
import com.fourigin.argo.forms.initialization.ExternalValueResolverFactory;
import com.fourigin.argo.forms.normalizer.mapping.DataNormalizerModule;
import com.fourigin.argo.forms.prepopulation.DummyNameplatePrePopulationValueResolver;
import com.fourigin.argo.forms.prepopulation.PrePopulationValuesResolver;
import com.fourigin.argo.forms.prepopulation.StoredEntryPrePopulationValueResolver;
import com.fourigin.argo.forms.processing.FulfillInternalCardFormEntryProcessor;
import com.fourigin.argo.forms.processing.FulfillTaxPaymentFormEntryProcessor;
import com.fourigin.argo.forms.processing.FulfillVehicleRegistrationFormEntryProcessor;
import com.fourigin.argo.forms.processing.customer.CreateCustomerFormsEntryProcessor;
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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@ComponentScan("com.fourigin.argo.forms.config")
public class NettyServer {

    private int port;

    private String contextPath;

    public static void main(String... args) throws Exception {
        SpringApplication app = new SpringApplication(NettyServer.class);
        app.addListeners(
            new ApplicationPidFileWriter()  // DEFAULT: application.pid
        );

        ConfigurableApplicationContext context = app.run(args);

        Channel channel = context.getBean(Channel.class);

        channel.closeFuture().sync();
    }

    @Bean
    public ServerBootstrap serverBootstrap(
        @Autowired CustomerRepository customerRepository,
        @Autowired FormsStoreRepository formsStoreRepository,
        @Autowired FormDefinitionRepository formDefinitionRepository,
        @Autowired FormsProcessingDispatcher formsProcessingDispatcher,
        @Autowired ExternalValueResolverFactory externalValueResolverFactory,
        @Autowired(required = false) Set<PrePopulationValuesResolver> prePopulationValuesResolvers,
        @Autowired MessageSource messageSource
    ) {
        return new ServerBootstrap()
            .group(eventLoopGroup())
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new HttpServerInitializer(
                contextPath,
                customerRepository,
                formsStoreRepository,
                formDefinitionRepository,
                formsProcessingDispatcher,
                externalValueResolverFactory,
                prePopulationValuesResolvers,
                messageSource,
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
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.registerModule(new PaymentModule());
        objectMapper.registerModule(new DataNormalizerModule());
        objectMapper.registerModule(new DataFormatterModule());
        return objectMapper;
    }

    @Bean
    public FormsEntryProcessorMapping formsEntryProcessorMapping() {
        FormsEntryProcessorMapping mapping = new FormsEntryProcessorMapping();

        mapping.put("register-customer", Collections.singletonList(
            CreateCustomerFormsEntryProcessor.NAME
        ));

        mapping.put("register-vehicle", Arrays.asList(
            FulfillVehicleRegistrationFormEntryProcessor.NAME,
            FulfillTaxPaymentFormEntryProcessor.NAME,
            FulfillInternalCardFormEntryProcessor.NAME
        ));

        return mapping;
    }

    @Bean
    public FormsEntryProcessorFactory formsEntryProcessorFactory(
        @Autowired FormsStoreRepository formsStoreRepository,
        @Autowired CustomerRepository customerRepository,
        @Value("${forms.vehicle-registration.registration-form}") File registrationForm,
        @Value("${forms.vehicle-registration.tax-payment-form}") File taxPaymentForm,
        @Value("${forms.vehicle-registration.registration-card-form}") File internalForm
    ) {
        if (!registrationForm.exists()) {
            throw new IllegalArgumentException("Original vehicle registration form '" + registrationForm.getAbsolutePath() + "' not found!");
        }

        if (!taxPaymentForm.exists()) {
            throw new IllegalArgumentException("Original tax payment form '" + taxPaymentForm.getAbsolutePath() + "' not found!");
        }

        CreateCustomerFormsEntryProcessor createCustomerFormsEntryProcessor = new CreateCustomerFormsEntryProcessor(
            formsStoreRepository,
            customerRepository
        );

        FulfillVehicleRegistrationFormEntryProcessor fulfillFormEntryProcessor = new FulfillVehicleRegistrationFormEntryProcessor(
            formsStoreRepository,
            customerRepository,
            registrationForm
        );

        FulfillTaxPaymentFormEntryProcessor fulfillTaxPaymentEntryProcessor = new FulfillTaxPaymentFormEntryProcessor(
            formsStoreRepository,
            customerRepository,
            taxPaymentForm
        );

        FulfillInternalCardFormEntryProcessor fulfillInternalCardFormEntryProcessor = new FulfillInternalCardFormEntryProcessor(
            formsStoreRepository,
            customerRepository,
            internalForm
        );

        Map<String, FormsEntryProcessor> processors = new HashMap<>();
        processors.put(CreateCustomerFormsEntryProcessor.NAME, createCustomerFormsEntryProcessor);
        processors.put(FulfillVehicleRegistrationFormEntryProcessor.NAME, fulfillFormEntryProcessor);
        processors.put(FulfillTaxPaymentFormEntryProcessor.NAME, fulfillTaxPaymentEntryProcessor);
        processors.put(FulfillInternalCardFormEntryProcessor.NAME, fulfillInternalCardFormEntryProcessor);

        return new DefaultFormsEntryProcessorFactory(processors);
    }

    @Bean
    public FormsRegistry formsRegistry() {
        return new FormsRegistry();
    }

    @Bean
    public FormsProcessingDispatcher formsProcessingDispatcher(
        @Autowired FormsStoreRepository formsStoreRepository,
        @Autowired FormsEntryProcessorMapping formsEntryProcessorMapping,
        @Autowired FormsEntryProcessorFactory formsEntryProcessorFactory,
        @Autowired FormsRegistry formsRegistry
    ) {
        return new DefaultFormsProcessingDispatcher(
            formsStoreRepository,
            formsEntryProcessorMapping,
            formsEntryProcessorFactory,
            formsRegistry
        );
    }

    @Bean
    public ExternalValueResolverFactory externalValueResolverFactory(
        @Autowired CustomerRepository customerRepository
    ) {
        ExternalValueResolverFactory externalValueResolverFactory = new ExternalValueResolverFactory();

        externalValueResolverFactory.put("customer", new CustomerExternalValueResolver(customerRepository));

        return externalValueResolverFactory;
    }

    @Bean
    Set<PrePopulationValuesResolver> prePopulationValuesResolvers() {
        return new HashSet<>(Arrays.asList(
            new StoredEntryPrePopulationValueResolver("STORED_DATA"),
            new DummyNameplatePrePopulationValueResolver("DEFAULTS")
        ));
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
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
