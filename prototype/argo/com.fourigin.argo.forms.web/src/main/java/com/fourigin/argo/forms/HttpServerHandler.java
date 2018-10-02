package com.fourigin.argo.forms;

import com.google.common.io.Resources;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String contextPath;

    private final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public HttpServerHandler(String contextPath) {
        this.contextPath = contextPath;

        if (logger.isInfoEnabled()) logger.info("Initialized with contextPath '{}'", contextPath);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();

        if (logger.isInfoEnabled()) logger.info("Channel read uri: '{}'", uri);

        if(!uri.startsWith(contextPath)){
            if (logger.isInfoEnabled()) logger.info("Unmatched uri '{}', should start with context path '{}'!", uri, contextPath);
            serve404(ctx);
            return;
        }

        if((contextPath + "/").equals(uri)){
            serveStatic(ctx, "/index.html");
            return;
        }

        if((contextPath + "/data").equals(uri)){
            serveData(ctx);
            return;
        }

        serveStatic(ctx, uri.substring(contextPath.length()));
    }

    private void serveData(ChannelHandlerContext ctx) {
        if (logger.isInfoEnabled()) logger.info("Serving data ...");

        Map<Long, String> data = new HashMap<>();
        data.put(1L, "One");
        data.put(2L, "Two");
        data.put(3L, "Three");

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int num = 0;
        int size = data.size();
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            sb.append('"').append(entry.getValue()).append('"');
            num++;
            if (num < size) {
                sb.append(",");
            }
        }
        sb.append("]");

        ByteBuf content = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.write(response);
    }

    private void serveStatic(ChannelHandlerContext ctx, String path) throws Exception {
        try {
            byte[] raw = Resources.toByteArray(Resources.getResource(path.substring(1)));
            ByteBuf content = Unpooled.wrappedBuffer(raw);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            ctx.write(response);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            serve404(ctx);
        }

    }

    private void serve404(ChannelHandlerContext ctx) {
        try {
            ByteBuf content = Unpooled.wrappedBuffer("Resource no found!".getBytes(StandardCharsets.UTF_8));

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, content);
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
