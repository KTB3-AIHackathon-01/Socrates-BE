package com.socrates.app.webflux.chat.domain.port.out;

import reactor.core.publisher.Mono;

public interface ConversationCounterPort {
	Mono<Long> increment();

	Mono<Long> get();

	Mono<Void> reset();
}
