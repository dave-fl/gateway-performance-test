package io.spring.sample.webfluxfnbenchmark.services;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataBufferGeneratorService {

    private static final char PREFIX = 'A';
    private final Map<Integer, DataBuffer> generatedBuffers = new ConcurrentHashMap<>();
    private final NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    public DataBuffer getGeneratedString(Integer length) {
        DataBuffer buffer = generatedBuffers.get(length);
        if (buffer == null) {
            buffer = generatedBuffers.computeIfAbsent(length, l -> {
                ByteBuf byteBuf = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(StringUtils.repeat(PREFIX, l).getBytes(StandardCharsets.UTF_8)));
                return nettyDataBufferFactory.wrap(byteBuf);
            });
        }
        return buffer;
    }
}
