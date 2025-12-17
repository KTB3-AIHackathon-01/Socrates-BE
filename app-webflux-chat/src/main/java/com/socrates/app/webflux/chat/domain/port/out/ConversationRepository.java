package com.socrates.app.webflux.chat.domain.port.out;

import com.socrates.app.webflux.chat.domain.model.conversation.ConversationTurn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationRepository {
	Mono<ConversationTurn> save(ConversationTurn turn);

	Flux<ConversationTurn> findRecent(int limit);

	Flux<ConversationTurn> findAll();
}
