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

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

final class RouterFunctionConfig {

	private static final char PREFIX = "A".charAt(0);
	private static final Map<Integer, byte[]> generatedStrings = new ConcurrentHashMap<>();

	private static byte[] repeat(final char ch, final int repeat) {
		if (repeat <= 0) {
			return "".getBytes(CharsetUtil.ISO_8859_1);
		}
		final char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf).getBytes(CharsetUtil.ISO_8859_1);
	}


	static Consumer<? super HttpServerRoutes> routesBuilder() {
		return r -> r.get("/demo/{length}", text(false))
				.get("/demo/{length}/{delay}", text(true));
	}

	static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> text(boolean hasDelay) {
		return (req, res) -> {
			int length = Integer.parseInt(req.param("length"));
			String delay = req.param("delay");

			byte[] strBytes = generatedStrings.get(length);
			if (strBytes == null) {
				strBytes = generatedStrings.computeIfAbsent(length, l -> repeat(PREFIX, l));
			}

			if (hasDelay) {
				return res.header("Content-Type", "text/plain")
						.send(Mono.just(Unpooled.wrappedBuffer(strBytes))
								.delayElement(Duration.ofMillis(Long.parseLong(delay))));
			}
			return res.header("Content-type", "text/plain")
					.sendObject(Unpooled.wrappedBuffer(strBytes));
		};
	}
}