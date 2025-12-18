package com.socrates.app.mvc.analytics.chat.repository;

import com.socrates.app.mvc.analytics.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>,
        ChatMessageRepositoryCustom {

    Page<ChatMessage> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable);

    long countByUserId(String userId);

    Optional<ChatMessage> findTopByUserIdOrderByCreatedAtDesc(String userId);
}
