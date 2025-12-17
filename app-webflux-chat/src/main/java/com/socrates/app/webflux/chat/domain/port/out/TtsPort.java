package com.socrates.app.webflux.chat.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TtsPort {
	Flux<byte[]> streamSynthesize(String text);

	Mono<byte[]> synthesize(String text);

	default Mono<Void> prepare() {
		return Mono.empty();
	}
}
