package com.socrates.app.webflux.chat.domain.port.out;

import com.socrates.app.webflux.chat.domain.model.llm.CompletionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LlmPort {
	Flux<String> streamCompletion(CompletionRequest request);

	Mono<String> complete(CompletionRequest request);
}
