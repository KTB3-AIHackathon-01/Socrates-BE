package com.socrates.app.webflux.chat.repository;

import com.socrates.app.webflux.chat.domain.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    Flux<ChatMessage> findByUserIdAndSessionIdOrderByCreatedAtDesc(String userId, String sessionId);

    Flux<ChatMessage> findByUserIdOrderByCreatedAtDesc(String userId);
}
