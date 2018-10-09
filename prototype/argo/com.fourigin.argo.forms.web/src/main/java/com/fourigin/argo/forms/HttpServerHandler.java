package com.fourigin.argo.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.definition.FormDefinition;
import com.fourigin.argo.forms.model.FormsRequest;
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

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String contextPath;

    private ObjectMapper objectMapper;

    private FormsStoreRepository formsStoreRepository;

    private FormDefinitionRepository formDefinitionRepository;

    private static final String URI_REGISTER_FORM = "/register-form";

    private static final String URI_PRE_VALIDATE_FORM = "/pre-validate";

    private static final String URI_VALIDATE_FORM = "/validate";

    private final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public HttpServerHandler(
        String contextPath,
        FormDefinitionRepository formDefinitionRepository, FormsStoreRepository formsStoreRepository,
        ObjectMapper objectMapper
    ) {
        this.contextPath = contextPath;
        this.formsStoreRepository = formsStoreRepository;
        this.formDefinitionRepository = formDefinitionRepository;
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

        if ((contextPath + URI_REGISTER_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing register-form request");
            serveRegisterForm(ctx, request);
            return;
        }

        if ((contextPath + URI_PRE_VALIDATE_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing pre-validate form request");
            servePreValidateForm(ctx, request);
            return;
        }

        if ((contextPath + URI_VALIDATE_FORM).equals(uri)) {
            if (logger.isDebugEnabled()) logger.debug("Processing validate -form request");
            serveValidateForm(ctx, request);
            return;
        }

        serve404(ctx, uri.substring(contextPath.length()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void serveRegisterForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving register-form ...");

        try {
            FormsRequest formsRequest = getRequestBody(FormsRequest.class, request);

            FormsStoreEntry entry = new FormsStoreEntry();
            entry.setData(formsRequest.getData());
            String entryId = formsStoreRepository.createEntry(entry);

            formsStoreRepository.createEntryInfo(entryId, formsRequest.getHeader());

            ByteBuf content = Unpooled.copiedBuffer(entryId, CharsetUtil.UTF_8);
            writeResponseBody(ctx, content, HttpResponseStatus.OK);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

    private void servePreValidateForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving pre-validate form ...");

        try {
            FormsRequest formsRequest = getRequestBody(FormsRequest.class, request);
            FormsEntryHeader header = formsRequest.getHeader();
            String formDefinitionId = header.getFormDefinition();

            FormDefinition formDefinition = formDefinitionRepository.retrieve(formDefinitionId);

            FormData formData = new FormData();
            formData.setFormDefinitionId(formDefinitionId);
            formData.setFormId(formsRequest.getFormId());
            formData.setPreValidation(true);
            formData.setValidateFields(formsRequest.getData());
            FormValidationResult result = FormsValidator.validate(formDefinition, formData);

            if(result.isValid()){
                writeResponseBody(ctx, result, HttpResponseStatus.OK);
            }
            else {
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

            FormDefinition formDefinition = formDefinitionRepository.retrieve(formDefinitionId);

            FormData formData = new FormData();
            formData.setFormDefinitionId(formDefinitionId);
            formData.setFormId(formsRequest.getFormId());
            formData.setPreValidation(false);
            formData.setValidateFields(formsRequest.getData());
            FormValidationResult result = FormsValidator.validate(formDefinition, formData);

            if(result.isValid()){
                writeResponseBody(ctx, result, HttpResponseStatus.OK);
            }
            else {
                writeResponseBody(ctx, result, HttpResponseStatus.NOT_ACCEPTABLE);
            }
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error!", th);
            serve500(ctx, th);
        }
    }

//    private void serveData(ChannelHandlerContext ctx) {
//        if (logger.isInfoEnabled()) logger.info("Serving data ...");
//
//        Map<Long, String> data = new HashMap<>();
//        data.put(1L, "One");
//        data.put(2L, "Two");
//        data.put(3L, "Three");
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("[");
//        int num = 0;
//        int size = data.size();
//        for (Map.Entry<Long, String> entry : data.entrySet()) {
//            sb.append('"').append(entry.getValue()).append('"');
//            num++;
//            if (num < size) {
//                sb.append(",");
//            }
//        }
//        sb.append("]");
//
//        ByteBuf content = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
//        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
//        ctx.write(response);
//    }
//
//    private void serveJsonInput(ChannelHandlerContext ctx, FullHttpRequest msg) {
//        if (logger.isInfoEnabled()) logger.info("Serving json input ...");
//
//        String input = msg.content().toString(StandardCharsets.UTF_8);
//        if (logger.isInfoEnabled()) logger.info("input: {}", input);
//
//        Object value;
//        try {
//            value = objectMapper.readValue(input, Object.class);
//        } catch (Throwable th) {
//            if (logger.isErrorEnabled())
//                logger.error("Unable to map object from string '{}': {}", input, th.getMessage());
//            value = null;
//        }
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("{");
//        sb.append("\"processed\":true,");
//        if (value != null) {
//            sb.append("\"input\":\"").append(value.toString()).append("\"");
//        } else {
//            sb.append("\"input\":null");
//        }
//        sb.append("}");
//
//        ByteBuf content = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
//        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
//        ctx.write(response);
//    }

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
        String message = "Resource no found: " + msg;
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
