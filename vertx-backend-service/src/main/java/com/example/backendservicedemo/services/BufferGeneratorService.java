package com.example.backendservicedemo.services;

import io.vertx.core.buffer.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BufferGeneratorService {

    private static final char PREFIX = 'A';
    private final Map<Integer, Buffer> generatedBuffers = new ConcurrentHashMap<>();

    public Buffer generateBuffer(Integer length) {
        Buffer buffer = generatedBuffers.get(length);
        if (buffer == null) {
            buffer = generatedBuffers.computeIfAbsent(length, l -> Buffer.buffer(StringUtils.repeat(PREFIX, l)));
        }
        return buffer;
    }
}
