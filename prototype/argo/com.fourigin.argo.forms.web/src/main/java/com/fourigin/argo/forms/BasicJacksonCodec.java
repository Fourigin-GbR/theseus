package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class BasicJacksonCodec<T> extends ByteToMessageCodec<T> {
    private final Class<T> clazz;
    private final ObjectMapper objectMapper;

    public BasicJacksonCodec(Class<T> clazz, ObjectMapper objectMapper) {
        super(clazz);
        this.clazz = clazz;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws IOException {
        ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(out);
        objectMapper.writeValue(((DataOutput) byteBufOutputStream), msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
        out.add(objectMapper.readValue(((DataInput) byteBufInputStream), clazz));
    }
}
