package com.fourigin.argo.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.definition.FormDefinition;
import com.fourigin.argo.forms.definition.FormObjectDefinition;
import com.fourigin.argo.forms.definition.FormObjectMapperDefinition;
import com.fourigin.argo.forms.mapping.FormObjectMapper;
import com.fourigin.argo.forms.model.FormsRequest;
import com.fourigin.argo.forms.models.Attachment;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.validation.FormData;
import com.fourigin.argo.forms.validation.FormValidationResult;
import com.fourigin.argo.forms.validation.FormsValidator;
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
import io.netty.util.CharsetUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String contextPath;

    private ObjectMapper objectMapper;

    private FormsStoreRepository formsStoreRepository;

    private FormDefinitionRepository formDefinitionRepository;

    private FormsProcessingDispatcher formsProcessingDispatcher;

    private static final String URI_REGISTER_FORM = "/register-form";

    private static final String URI_PRE_VALIDATE_FORM = "/pre-validate";

    private static final String URI_VALIDATE_FORM = "/validate";

    private final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public HttpServerHandler(
        String contextPath,
        FormDefinitionRepository formDefinitionRepository,
        FormsStoreRepository formsStoreRepository,
        FormsProcessingDispatcher formsProcessingDispatcher,
        ObjectMapper objectMapper
    ) {
        this.contextPath = contextPath;
        this.formsStoreRepository = formsStoreRepository;
        this.formDefinitionRepository = formDefinitionRepository;
        this.formsProcessingDispatcher = formsProcessingDispatcher;
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

        if ((contextPath + URI_REGISTER_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing register-form request");
            serveRegisterForm(ctx, request);
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
            FormValidationResult result = internalValidate(formsRequest, true);

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
            FormValidationResult result = internalValidate(formsRequest, false);

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

    private void serveRegisterForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving register-form ...");

        try {
            FormsRequest formsRequest = getRequestBody(FormsRequest.class, request);
            if (logger.isDebugEnabled()) logger.debug("Processing form {}", formsRequest);

            // validate form
            FormValidationResult validationResult = internalValidate(formsRequest, false);

            if (!validationResult.isValid()) {
                if (logger.isDebugEnabled()) logger.debug("Validation *not* successful!");
                writeResponseBody(ctx, validationResult, HttpResponseStatus.NOT_ACCEPTABLE);
                return;
            }

            // store form data
            if (logger.isDebugEnabled()) logger.debug("Validation successful, storing the form");
            FormsStoreEntry entry = new FormsStoreEntry();
            entry.setData(formsRequest.getData());
            String entryId = formsStoreRepository.createEntry(entry);

            formsStoreRepository.createEntryInfo(entryId, formsRequest.getHeader());

            // map form entry to defined objects
            FormDefinition formDefinition = validationResult.getFormDefinition();
            Map<String, FormObjectDefinition> objectMappings = formDefinition.getObjectMappings();
            if (objectMappings != null && !objectMappings.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Map form objects");

                for (Map.Entry<String, FormObjectDefinition> objectDefinitionEntry : objectMappings.entrySet()) {
                    String objectName = objectDefinitionEntry.getKey();
                    FormObjectDefinition objectDefinition = objectDefinitionEntry.getValue();
                    Object mappedObject = mapObject(objectDefinition, entry);

                    Attachment attachment = new Attachment();
                    attachment.setPayload(mappedObject);
                    attachment.setTimestamp(System.currentTimeMillis());
                    attachment.setProducer("/");
                    if (logger.isDebugEnabled()) logger.debug("Adding an attachment {}", attachment);

                    formsStoreRepository.addAttachment(entryId, objectName, attachment);
                }
            }

            // start processing form entry
            formsProcessingDispatcher.registerFormEntry(entryId);

            // generate response
            ByteBuf content = Unpooled.copiedBuffer(entryId, CharsetUtil.UTF_8);
            writeResponseBody(ctx, content, HttpResponseStatus.OK);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private Object mapObject(FormObjectDefinition objectDefinition, FormsStoreEntry entry) {
        String targetType = objectDefinition.getType();
        Class<?> targetClass;
        try {
            targetClass = Class.forName(targetType);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Unable to resolve target class name '" + targetType + "'!", ex);
        }

        FormObjectMapperDefinition mappingDefinition = objectDefinition.getMapper();
        String mapperType = mappingDefinition.getType();
        Class mapperClass;
        try {
            mapperClass = Class.forName(mapperType);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Unable to resolve mapper class name '" + mapperType + "'!", ex);
        }

        if (!FormObjectMapper.class.isAssignableFrom(mapperClass)) {
            throw new IllegalArgumentException("Mapper type " + mapperType + " doesn't match FormObjectMapper's interface!");
        }

        Object mapperObject;
        try {
            mapperObject = mapperClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Unable to instantiate class for name '" + mapperType + "'!", ex);
        }

        FormObjectMapper formObjectMapper = (FormObjectMapper) mapperObject;

        Map<String, Object> mapperSettings = mappingDefinition.getSettings();
        formObjectMapper.initialize(mapperSettings);

        return formObjectMapper.parseValue(targetClass, entry);
    }

    private FormValidationResult internalValidate(FormsRequest formsRequest, boolean preValidation) {
        FormsEntryHeader header = formsRequest.getHeader();
        String formDefinitionId = header.getFormDefinition();

        FormDefinition formDefinition = formDefinitionRepository.retrieveDefinition(formDefinitionId);
        if (formDefinition == null) {
            throw new IllegalArgumentException("No form-definition found for id '" + formDefinitionId + "'!");
        }

        FormData formData = new FormData();
        formData.setFormDefinitionId(formDefinitionId);
        formData.setFormId(formsRequest.getFormId());
        formData.setPreValidation(preValidation);
        formData.setValidateFields(formsRequest.getData());
        return FormsValidator.validate(formDefinition, formData);
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
