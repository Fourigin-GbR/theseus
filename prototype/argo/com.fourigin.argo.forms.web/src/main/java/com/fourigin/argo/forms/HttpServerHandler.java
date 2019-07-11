package com.fourigin.argo.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.definition.ExternalValueReference;
import com.fourigin.argo.forms.definition.FieldDefinition;
import com.fourigin.argo.forms.definition.FormDefinition;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.formatter.DataFormatter;
import com.fourigin.argo.forms.initialization.ExternalValueResolver;
import com.fourigin.argo.forms.initialization.ExternalValueResolverFactory;
import com.fourigin.argo.forms.mapping.FormObjectMapper;
import com.fourigin.argo.forms.model.CleanupRequest;
import com.fourigin.argo.forms.model.FormsRequest;
import com.fourigin.argo.forms.model.InitRequest;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.argo.forms.normalizer.DataNormalizer;
import com.fourigin.argo.forms.prepopulation.PrePopulationType;
import com.fourigin.argo.forms.prepopulation.PrePopulationValuesResolver;
import com.fourigin.argo.forms.validation.ValidationMessage;
import com.fourigin.argo.forms.validation.FormData;
import com.fourigin.argo.forms.validation.FormFieldValidationResult;
import com.fourigin.argo.forms.validation.FormValidationResult;
import com.fourigin.argo.forms.validation.FormsValidator;
import com.fourigin.utilities.core.TriConsumer;
import com.fourigin.utilities.reflection.InitializableObjectDescriptor;
import com.fourigin.utilities.reflection.ObjectInitializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String contextPath;

    private ObjectMapper objectMapper;

    private MessageSource messageSource;

    private CustomerRepository customerRepository;

    private FormsStoreRepository formsStoreRepository;

    private FormDefinitionResolver formDefinitionResolver;

//    private FormsProcessingDispatcher formsProcessingDispatcher;

    private ExternalValueResolverFactory externalValueResolverFactory;

    private Set<PrePopulationValuesResolver> prePopulationValuesResolvers;

    private static final String URI_INITIALIZE_FORM = "/init-form";

    private static final String URI_PRE_POPULATE_FORM = "/pre-populate";

    private static final String URI_REGISTER_FORM = "/register-form";

    private static final String URI_PRE_VALIDATE_FORM = "/pre-validate";

    private static final String URI_VALIDATE_FORM = "/validate";

    private static final String URI_CLEANUP_DATA = "/cleanup-data";

    private final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public HttpServerHandler(
        String contextPath,
        CustomerRepository customerRepository,
        FormDefinitionResolver formDefinitionResolver,
        FormsStoreRepository formsStoreRepository,
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

        if (logger.isInfoEnabled()) logger.info("Initialized with contextPath '{}'", contextPath);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();

        if (logger.isInfoEnabled()) logger.info("Channel read uri: '{}'", uri);

        if (!uri.startsWith(contextPath)) {
            if (logger.isInfoEnabled())
                logger.info("Unmatched uri '{}', should start with context path '{}'!", uri, contextPath);
            serve404(ctx, uri);
            return;
        }

        if ((contextPath + "/").equals(uri)) {
            serveStatic(ctx, "/index.html");
            return;
        }

        if ((contextPath + URI_PRE_VALIDATE_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing pre-validate form request");
            servePreValidateForm(ctx, request);
            return;
        }

        if ((contextPath + URI_VALIDATE_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing validate-form request");
            serveValidateForm(ctx, request);
            return;
        }

        if ((contextPath + URI_INITIALIZE_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing init-form request");
            serveInitializeForm(ctx, request);
            return;
        }

        if ((contextPath + URI_PRE_POPULATE_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing pre-populate-form request");
            servePrePopulateForm(ctx, request);
            return;
        }

        if ((contextPath + URI_REGISTER_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing register-form request");
            serveRegisterForm(ctx, request);
            return;
        }

        if ((contextPath + URI_CLEANUP_DATA).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing cleanup-data request");
            serveCleanupData(ctx, request);
            return;
        }

        serve404(ctx, uri.substring(contextPath.length()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void servePreValidateForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving pre-validate form ...");

        try {
            FormsRequest formsRequest = getRequestBody(FormsRequest.class, request);

            FormsEntryHeader header = formsRequest.getHeader();
            String formDefinitionId = header.getFormDefinition();

            ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

            FormDefinition formDefinition = formDefinitionResolver.retrieveDefinition(formDefinitionId, stages.firstName());
            if (formDefinition == null) {
                throw new IllegalArgumentException("No form-definition found for id '" + formDefinitionId + "'!");
            }

            Map<String, String> normalizedData = normalizeData(formsRequest.getData(), formDefinition);

            FormValidationResult result = internalValidate(
                formsRequest.getFormId(),
                header,
                normalizedData,
                formDefinition,
                true
            );

            if (result.isValid()) {
                writeResponseBody(ctx, result, HttpResponseStatus.OK);
            } else {
                writeResponseBody(ctx, result, HttpResponseStatus.NOT_ACCEPTABLE);
            }
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private void serveValidateForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving validate form ...");

        try {
            FormsRequest formsRequest = getRequestBody(FormsRequest.class, request);

            FormsEntryHeader header = formsRequest.getHeader();
            String formDefinitionId = header.getFormDefinition();

            ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

            FormDefinition formDefinition = formDefinitionResolver.retrieveDefinition(formDefinitionId, stages.firstName());
            if (formDefinition == null) {
                throw new IllegalArgumentException("No form-definition found for id '" + formDefinitionId + "'!");
            }

            Map<String, String> normalizedData = normalizeData(formsRequest.getData(), formDefinition);

            FormValidationResult result = internalValidate(
                formsRequest.getFormId(),
                header,
                normalizedData,
                formDefinition,
                false
            );

            if (result.isValid()) {
                writeResponseBody(ctx, result, HttpResponseStatus.OK);
            } else {
                writeResponseBody(ctx, result, HttpResponseStatus.NOT_ACCEPTABLE);
            }
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private void serveInitializeForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving init form ...");

        try {
            InitRequest initRequest = getRequestBody(InitRequest.class, request);

            String formDefinitionId = initRequest.getFormDefinition();

            ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

            FormDefinition formDefinition = formDefinitionResolver.retrieveDefinition(formDefinitionId, stages.firstName());

            String customerId = initRequest.getCustomer();

            Map<String, Map<String, String>> result = new HashMap<>();

            Map<String, FieldDefinition> fields = formDefinition.getFields();
            initializeFields(customerId, fields, result);

            writeResponseBody(ctx, result, HttpResponseStatus.OK);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private void servePrePopulateForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving pre-populate form ...");

        try {
            InitRequest initRequest = getRequestBody(InitRequest.class, request);

            String formDefinitionId = initRequest.getFormDefinition();
            String customerId = initRequest.getCustomer();
            String entryId = initRequest.getEntryId();

            Map<String, Map<String, String>> result = new HashMap<>();

            prePopulateFields(formDefinitionId, customerId, entryId, result);

            writeResponseBody(ctx, result, HttpResponseStatus.OK);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private void serveRegisterForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving register-form ...");

        try {
            FormsRequest formsRequest = getRequestBody(FormsRequest.class, request);
            if (logger.isDebugEnabled()) logger.debug("Processing form {}", formsRequest);

            FormsEntryHeader header = formsRequest.getHeader();
            String formDefinitionId = header.getFormDefinition();

            ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);
            String firstStageName = stages.firstName();

            FormDefinition formDefinition = formDefinitionResolver.retrieveDefinition(formDefinitionId, firstStageName);
            if (formDefinition == null) {
                throw new IllegalArgumentException("No form-definition found for id '" + formDefinitionId + "'!");
            }

            Map<String, String> normalizedData = normalizeData(formsRequest.getData(), formDefinition);
            if (logger.isDebugEnabled()) logger.debug("Normalized data: {}", normalizedData);

            // validate form
            FormValidationResult validationResult = internalValidate(
                formsRequest.getFormId(),
                header,
                normalizedData,
                formDefinition,
                false
            );

            if (!validationResult.isValid()) {
                if (logger.isDebugEnabled()) logger.debug("Validation *not* successful!");

                writeResponseBody(ctx, validationResult, HttpResponseStatus.NOT_ACCEPTABLE);
                return;
            }

            Map<String, String> formattedData = formatData(normalizedData, formDefinition);
            if (logger.isDebugEnabled()) logger.debug("Formatted data: {}", formattedData);

            // store form data
            if (logger.isDebugEnabled()) logger.debug("Validation successful, storing the form");
            FormsStoreEntry entry = new FormsStoreEntry();
            entry.setData(formattedData);
            String entryId = formsStoreRepository.createEntry(entry);

            FormsStoreEntryInfo entryInfo = formsStoreRepository.createEntryInfo(entryId, header);

            // map form entry to defined objects
            Map<String, InitializableObjectDescriptor> objectMappings = formDefinition.getObjectMappings();
            if (objectMappings != null && !objectMappings.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Map form objects");

                for (Map.Entry<String, InitializableObjectDescriptor> objectDefinitionEntry : objectMappings.entrySet()) {
                    String objectName = objectDefinitionEntry.getKey();
                    InitializableObjectDescriptor objectDefinition = objectDefinitionEntry.getValue();
                    Object mappedObject = mapObject(objectDefinition, entry, entryId);

                    formsStoreRepository.addObjectAttachment(entryId, objectName, mappedObject);
                }
            }

            // start processing form entry
            formsStoreRepository.updateProcessingState(entryInfo, ProcessingState.PENDING);

            // generate response
            Map<String, String> result = new HashMap<>();
            result.put("id", entryId);
            writeResponseBody(ctx, result, HttpResponseStatus.OK);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private void serveCleanupData(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving cleanup data ...");

        try {
            CleanupRequest cleanupRequest = getRequestBody(CleanupRequest.class, request);

            String formDefinitionId = cleanupRequest.getFormDefinitionId();

            ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

            FormDefinition formDefinition = formDefinitionResolver.retrieveDefinition(formDefinitionId, stages.firstName());
            if (formDefinition == null) {
                throw new IllegalArgumentException("No form-definition found for id '" + formDefinitionId + "'!");
            }

            Map<String, String> normalizedData = normalizeData(cleanupRequest.getData(), formDefinition);

            Map<String, String> formattedData = formatData(normalizedData, formDefinition);

            writeResponseBody(ctx, formattedData, HttpResponseStatus.OK);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private Map<String, String> normalizeData(Map<String, String> data, FormDefinition formDefinition) {
        Map<String, FieldDefinition> fields = formDefinition.getFields();
        if (fields == null || fields.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No fields found at all.");
            return data;
        }

        Map<String, InitializableObjectDescriptor> normalizerDescriptors = formDefinition.getDataNormalizers();
        if (normalizerDescriptors == null || normalizerDescriptors.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No data normalizers found.");
            return data;
        }

        final Map<String, DataNormalizer> normalizers = new HashMap<>();
        for (Map.Entry<String, InitializableObjectDescriptor> entry : normalizerDescriptors.entrySet()) {
            String normalizerName = entry.getKey();
            InitializableObjectDescriptor descriptor = entry.getValue();
            DataNormalizer normalizer = ObjectInitializer.initialize(descriptor);
            normalizers.put(normalizerName, normalizer);
        }

        final Map<String, String> result = new HashMap<>();

        processFields(
            formDefinition.getFields(),
            null,
            fieldDefinition -> true,
            (fieldName, currentPath, fieldDefinition) -> {
                String originalValue = data.get(currentPath);
                if (originalValue == null) {
                    if (logger.isDebugEnabled()) logger.debug("No value found for '{}'", currentPath);
                    return;
                }

                String normalizerName = fieldDefinition.getNormalizer();
                if (normalizerName == null) {
                    if (logger.isDebugEnabled())
                        logger.debug("No normalizer found for '{}', using original value '{}'", currentPath, originalValue);
                    result.put(currentPath, originalValue);
                    return;
                }

                DataNormalizer normalizer = normalizers.get(normalizerName);
                if (normalizer == null) {
                    throw new IllegalArgumentException("No data normalizer found for name '" + normalizerName + "'!");
                }

                String normalizedValue = normalizer.normalize(originalValue);
                if (logger.isDebugEnabled())
                    logger.debug("Normalized value of '{}': '{}' --> '{}'", currentPath, originalValue, normalizedValue);
                result.put(currentPath, normalizedValue);
            }
        );

        return result;
    }

    private Map<String, String> formatData(Map<String, String> data, FormDefinition formDefinition) {
        Map<String, FieldDefinition> fields = formDefinition.getFields();
        if (fields == null || fields.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No fields found at all.");
            return data;
        }

        final Map<String, InitializableObjectDescriptor> formatterDescriptors = formDefinition.getDataFormatters();
        if (formatterDescriptors == null || formatterDescriptors.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No data formatters found.");
            return data;
        }

        final Map<String, DataFormatter> formatters = new HashMap<>();
        for (Map.Entry<String, InitializableObjectDescriptor> entry : formatterDescriptors.entrySet()) {
            String formatterName = entry.getKey();
            InitializableObjectDescriptor descriptor = entry.getValue();
            DataFormatter formatter = ObjectInitializer.initialize(descriptor);
            formatters.put(formatterName, formatter);
        }

        final Map<String, String> result = new HashMap<>();

        processFields(
            formDefinition.getFields(),
            null,
            fieldDefinition -> true,
            (fieldName, currentPath, fieldDefinition) -> {
                String originalValue = data.get(currentPath);
                if (originalValue == null) {
                    if (logger.isDebugEnabled()) logger.debug("No value found for '{}'", currentPath);
                    return;
                }

                String formatterName = fieldDefinition.getFormatter();
                if (formatterName == null) {
                    if (logger.isDebugEnabled())
                        logger.debug("No formatter found for '{}', using original value '{}'", currentPath, originalValue);
                    result.put(currentPath, originalValue);
                    return;
                }

                DataFormatter formatter = formatters.get(formatterName);
                if (formatter == null) {
                    throw new IllegalArgumentException("No data formatter found for name '" + formatterName + "'!");
                }

                String formattedValue = formatter.format(originalValue);
                if (logger.isDebugEnabled())
                    logger.debug("Formatted value of '{}': '{}'", originalValue, formattedValue);
                result.put(currentPath, formattedValue);
            }
        );

        return result;
    }

    /*
    {
		"tax.account/stored-account": {
			"DE01 2345 6789 0123 45": "DE01 2345 6789 0123 45 (Deutsche Bank Berlin)",
			"DE66 5555 4444 3333 2222 11": "DE66 5555 4444 3333 2222 11 (Deutsche Bank Berlin)",
			"DE11222233334444555566": "DE11 2222 3333 4444 5555 66 (Dresdner Bank Dresden)"
		},
		...
	}
     */
    private void initializeFields(
        String customerId,
        Map<String, FieldDefinition> fields,
        Map<String, Map<String, String>> initValues
    ) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        processFields(
            fields,
            null,
            fieldDefinition -> fieldDefinition.getExternalValueReference() != null,
            (fieldName, currentPath, fieldDefinition) -> {
                ExternalValueReference externalValueReference = fieldDefinition.getExternalValueReference();
                String externalValueOwner = externalValueReference.getOwner();
                String externalValueKey = externalValueReference.getValue();

                ExternalValueResolver processor = externalValueResolverFactory.get(externalValueOwner);
                if (processor == null) {
                    throw new IllegalArgumentException("Unsupported external value resolver '" + externalValueOwner + "'!");
                }

                Map<String, String> externalValue = processor.resolveExternalValue(customerId, externalValueKey);
                initValues.put(currentPath, externalValue);
            }
        );
    }

    private void prePopulateFields(
        String formDefinitionId,
        String customerId,
        String entryId,
        Map<String, Map<String, String>> prePopulationValues
    ) {
        if (prePopulationValuesResolvers == null || prePopulationValuesResolvers.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("No pre-population value resolvers defined!");
            return;
        }

        if (entryId != null) {
            if (logger.isDebugEnabled()) logger.debug("Resolving pre-population values for entry '{}'", entryId);

            FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
            final FormsStoreEntry entry = formsStoreRepository.retrieveEntry(info);

            for (PrePopulationValuesResolver resolver : prePopulationValuesResolvers) {
                if (resolver.isAccepted(PrePopulationType.ENTRY)) {
                    Map<String, String> entryValues = resolver.resolveValues(entry);
                    if (entryValues != null) {
                        prePopulationValues.put(resolver.getName(), entryValues);
                    }
                }
            }
        }

        if (formDefinitionId != null) {
            if (logger.isDebugEnabled())
                logger.debug("Resolving pre-population values for formDefinition '{}'", formDefinitionId);

            ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

            FormDefinition formDefinition = formDefinitionResolver.retrieveDefinition(formDefinitionId, stages.firstName());

            for (PrePopulationValuesResolver resolver : prePopulationValuesResolvers) {
                if (resolver.isAccepted(PrePopulationType.FORM_DEFINITION)) {
                    Map<String, String> entryValues = resolver.resolveValues(formDefinition);
                    if (entryValues != null) {
                        prePopulationValues.put(resolver.getName(), entryValues);
                    }
                }
            }
        }

        if (customerId != null) {
            if (logger.isDebugEnabled()) logger.debug("Resolving pre-population values for customer '{}'", customerId);

            Customer customer = customerRepository.retrieveCustomer(customerId);

            for (PrePopulationValuesResolver resolver : prePopulationValuesResolvers) {
                if (resolver.isAccepted(PrePopulationType.CUSTOMER)) {
                    Map<String, String> entryValues = resolver.resolveValues(customer);
                    if (entryValues != null) {
                        prePopulationValues.put(resolver.getName(), entryValues);
                    }
                }
            }
        }
    }

    private void processFields(
        Map<String, FieldDefinition> fields,
        String parentPath,
        Predicate<FieldDefinition> tester,
        TriConsumer<String, String, FieldDefinition> consumer
    ) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String currentPath = parentPath == null ? fieldName : parentPath + "/" + fieldName;

            FieldDefinition fieldDefinition = entry.getValue();
            if (tester.test(fieldDefinition)) {
                consumer.accept(fieldName, currentPath, fieldDefinition);
            }

            Map<String, Map<String, FieldDefinition>> fieldValues = fieldDefinition.getValues();
            if (fieldValues != null) {
                for (Map.Entry<String, Map<String, FieldDefinition>> subEntries : fieldValues.entrySet()) {
                    Map<String, FieldDefinition> subFields = subEntries.getValue();
                    if (subFields != null) {
                        processFields(subFields, currentPath, tester, consumer);
                    }
                }
            }
        }
    }

    private Object mapObject(InitializableObjectDescriptor objectDefinition, FormsStoreEntry entry, String entryId) {
        String targetType = objectDefinition.getTargetClass();
        Class<?> targetClass;
        try {
            targetClass = Class.forName(targetType);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Unable to resolve target class name '" + targetType + "'!", ex);
        }

        Map<String, Object> settings = objectDefinition.getSettings();
        String scriptPath = (String) settings.get("file");
        FormObjectMapper formObjectMapper = new FormObjectMapper(customerRepository, scriptPath);

        return formObjectMapper.parseValue(targetClass, entry, entryId);
    }

    private FormValidationResult internalValidate(
        String formId,
        FormsEntryHeader header,
        Map<String, String> data,
        FormDefinition formDefinition,
        boolean preValidation
    ) {
        FormData formData = new FormData();
        formData.setFormDefinitionId(formDefinition.getForm());
        formData.setFormId(formId);
        formData.setPreValidation(preValidation);
        formData.setValidateFields(data);
        FormValidationResult result = FormsValidator.validate(formDefinition, formData);

        extendFormattedMessages(result, header.getLocale());

        return result;
    }

    private void extendFormattedMessages(FormValidationResult result, Locale locale) {
        if (result.isValid()) {
            return;
        }

        Map<String, FormFieldValidationResult> fields = result.getFields();
        for (Map.Entry<String, FormFieldValidationResult> entry : fields.entrySet()) {
            FormFieldValidationResult fieldResult = entry.getValue();

            List<ValidationMessage> hints = fieldResult.getHints();
            if (hints != null && !hints.isEmpty()) {
                // create formatted messages
                for (ValidationMessage hint : hints) {
                    hint.setFormattedMessage(messageSource.getMessage(
                        hint.getCode(),
                        hint.getArguments().toArray(),
                        locale
                    ));
                }
            }

            if (fieldResult.isValid()) {
                continue;
            }

            List<ValidationMessage> errorMessages = fieldResult.getErrorMessages();
            if (errorMessages == null || errorMessages.isEmpty()) {
                continue;
            }

            // create formatted error messages
            for (ValidationMessage message : errorMessages) {
                message.setFormattedMessage(messageSource.getMessage(
                    message.getCode(),
                    message.getArguments().toArray(),
                    locale
                ));
            }
        }
    }

    private void serveStatic(ChannelHandlerContext ctx, String path) throws Exception {
        URL resource = this.getClass().getResource(path);
        if (resource == null) {
            serve404(ctx, path);
            return;
        }

        byte[] raw = IOUtils.toByteArray(resource);
        writeBytes(ctx, raw, HttpResponseStatus.OK);
    }

    private void serve404(ChannelHandlerContext ctx, String msg) {
        String message = "Resource not found: " + msg;
        writeText(ctx, message, HttpResponseStatus.NOT_FOUND);
    }

    private void serve500(ChannelHandlerContext ctx, Throwable cause) {
        String message = "Unexpected error occurred: " + cause.getClass().getName() + ": " + cause.getMessage();
        writeText(ctx, message, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private <T> T getRequestBody(Class<T> target, FullHttpRequest request) {
        String input = request.content().toString(StandardCharsets.UTF_8);
        if (logger.isInfoEnabled()) logger.info("input: {}", input);

        try {
            return objectMapper.readValue(input, target);
        } catch (Throwable th) {
            throw new IllegalArgumentException("Unable to create " + target.getName() + " from '" + input + "'!", th);
        }
    }

    private void writeResponseBody(ChannelHandlerContext ctx, Object data, HttpResponseStatus status) {
        byte[] raw;
        try {
            raw = objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Error writing response body!", ex);
        }

        ByteBuf content = Unpooled.wrappedBuffer(raw);
        writeResponseBody(ctx, content, status);
    }

    private void writeResponseBody(ChannelHandlerContext ctx, ByteBuf content, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.write(response);
    }

    private void writeText(ChannelHandlerContext ctx, String message, HttpResponseStatus status) {
        byte[] raw = message.getBytes(StandardCharsets.UTF_8);
        writeBytes(ctx, raw, status);
    }

    private void writeBytes(ChannelHandlerContext ctx, byte[] raw, HttpResponseStatus status) {
        ByteBuf content = Unpooled.wrappedBuffer(raw);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.write(response);
    }
}
