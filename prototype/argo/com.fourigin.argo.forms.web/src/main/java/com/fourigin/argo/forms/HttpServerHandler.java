package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.model.FormsRequest;
import com.fourigin.argo.forms.models.FormsStoreEntry;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    private FormsStoreRepository formsStoreRepository;

    private static final String URI_REGISTER_FORM = "/register-form";

    private final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public HttpServerHandler(String contextPath, FormsStoreRepository formsStoreRepository) {
        this.contextPath = contextPath;
        this.formsStoreRepository = formsStoreRepository;

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

//        if ((contextPath + "/data").equals(uri)) {
//            serveData(ctx);
//            return;
//        }
//
//        if ((contextPath + "/jsonInput").equals(uri)) {
//            serveJsonInput(ctx, request);
//            return;
//        }

        serve404(ctx, uri.substring(contextPath.length()));
    }

    private void serveRegisterForm(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (logger.isInfoEnabled()) logger.info("Serving register-form ...");

        try {

            String input = request.content().toString(StandardCharsets.UTF_8);
            if (logger.isInfoEnabled()) logger.info("input: {}", input);

            FormsRequest formsRequest;
            try {
                formsRequest = objectMapper.readValue(input, FormsRequest.class);
            } catch (Throwable th) {
                if (logger.isErrorEnabled())
                    logger.error("Unable to map forms request from string '{}': {}", input, th.getMessage());
                serve500(ctx, th);
                return;
            }

            FormsStoreEntry entry = new FormsStoreEntry();
            entry.setData(formsRequest.getData());
            String entryId = formsStoreRepository.createEntry(entry);

            formsStoreRepository.createEntryInfo(entryId, formsRequest.getHeader());

            ByteBuf content = Unpooled.copiedBuffer(entryId, CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            ctx.write(response);
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

        ByteBuf content = Unpooled.wrappedBuffer(raw);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.write(response);
    }

    private void serve404(ChannelHandlerContext ctx, String msg) {
        try {
            String message = "Resource no found: " + msg;
            ByteBuf content = Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8));

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            ctx.write(response);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    private void serve500(ChannelHandlerContext ctx, Throwable cause) {
        try {
            String message = "Unexpected error occurred: " + cause.getClass().getName() + ": " + cause.getMessage();
            ByteBuf content = Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8));

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            ctx.write(response);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
