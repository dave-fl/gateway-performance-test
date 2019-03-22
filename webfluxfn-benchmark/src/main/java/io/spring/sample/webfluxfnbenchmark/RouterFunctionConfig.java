/*
 * Copyright 2002-2018 the original author or authors.
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

package io.spring.sample.webfluxfnbenchmark;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import io.spring.sample.webfluxfnbenchmark.services.StringGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class RouterFunctionConfig {

	@Autowired
	private StringGeneratorService stringGeneratorService;

	@Bean
	public RouterFunction<ServerResponse> routerFunctions() {
		return RouterFunctions.route()
				.GET("/demo", this::text)
				.build();
	}

	private Mono<ServerResponse> text(ServerRequest req) {
		Optional<String> delay = req.queryParam("delay");
		Optional<String> length = req.queryParam("length");

		String body = this.stringGeneratorService.getGeneratedString(Integer.parseInt(length.get()));
		Mono<ServerResponse> serverResponseMono = ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).syncBody(body);

		if (delay.isPresent()) {
			return serverResponseMono.delayElement(Duration.ofMillis(Long.parseLong(delay.get())));
		}
		return serverResponseMono;
	}
}
