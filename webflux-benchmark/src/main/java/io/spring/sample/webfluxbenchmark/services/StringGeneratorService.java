package io.spring.sample.webfluxbenchmark.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StringGeneratorService {

    private static final char PREFIX = 'A';
    private final Map<Integer, String> generatedStrings = new ConcurrentHashMap<>();

    public String getGeneratedString(Integer length) {
        String str = generatedStrings.get(length);
        if (str == null) {
            str = generatedStrings.computeIfAbsent(length, l -> StringUtils.repeat(PREFIX, l));
        }
        return str;
    }
}
