package io.spring.sample.webfluxbenchmark.services;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StringGeneratorService {

    private static final char PREFIX = "A".charAt(0);
    private final Map<Integer, String> generatedStrings = new ConcurrentHashMap<>();

    public String getGeneratedString(Integer length) {
        String str = generatedStrings.get(length);
        if (str == null) {
            str = generatedStrings.computeIfAbsent(length, l -> repeat(PREFIX, l));
        }
        return str;
    }

    private static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return "";
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }
}
