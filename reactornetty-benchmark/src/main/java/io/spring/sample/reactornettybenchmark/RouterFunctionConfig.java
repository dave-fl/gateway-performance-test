/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.sample.reactornettybenchmark;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

final class RouterFunctionConfig {

    private static final char PREFIX = 'A';
    private static final Map<Integer, ByteBuf> generatedBuffers = new ConcurrentHashMap<>();

    static Consumer<? super HttpServerRoutes> routesBuilder() {
        return r -> r.get("/demo/{length}", text(false))
                .get("/demo/{length}/{delay}", text(true));
    }

    static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> text(boolean hasDelay) {
        return (req, res) -> {
            int length = Integer.parseInt(req.param("length"));
            String delay = req.param("delay");

            ByteBuf buffer = generatedBuffers.get(length);
            if (buffer == null) {
                buffer = generatedBuffers.computeIfAbsent(length, l -> {
                    byte[] bytes = StringUtils.repeat(PREFIX, l).getBytes(CharsetUtil.ISO_8859_1);
                    return Unpooled.unreleasableBuffer(Unpooled.buffer(bytes.length).writeBytes(bytes));
                });
            }

            if (hasDelay) {
                return res.header("Content-Type", "text/plain")
                        .send(Mono.just(buffer)
                                .delayElement(Duration.ofMillis(Long.parseLong(delay))));
            }
            return res.header("Content-type", "text/plain")
                    .send(Mono.just(buffer));
        };
    }
}