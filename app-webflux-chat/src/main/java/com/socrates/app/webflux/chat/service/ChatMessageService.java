package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.domain.ChatMessage;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public Mono<ChatMessage> savePendingMessage(ChatRequest request) {
        ChatMessage message = ChatMessage.builder()
                .userId(request.getUserId())
                .sessionId(request.getSessionId())
                .userMessage(request.getMessage())
                .createdAt(LocalDateTime.now())
                .status(ChatMessage.MessageStatus.PENDING)
                .build();

        return chatMessageRepository.save(message)
                .doOnSuccess(saved -> log.debug("대기 중 메시지 저장 완료: {}", saved.getId()));
    }

    public Mono<ChatMessage> updateCompletedMessage(String messageId, String assistantMessage) {
        return updateCompletedMessage(messageId, assistantMessage, false);
    }

    public Mono<ChatMessage> updateCompletedMessage(String messageId, String assistantMessage, boolean isComplete) {
        return chatMessageRepository.findById(messageId)
                .flatMap(message -> {
                    message.setAssistantMessage(assistantMessage);
                    message.setCompletedAt(LocalDateTime.now());
                    message.setStatus(ChatMessage.MessageStatus.COMPLETED);
                    message.setIsComplete(isComplete);
                    return chatMessageRepository.save(message);
                })
                .doOnSuccess(saved -> log.debug("완료된 메시지 업데이트 완료: {}, isComplete: {}", saved.getId(), saved.getIsComplete()));
    }

    public Mono<ChatMessage> updateFailedMessage(String messageId) {
        return chatMessageRepository.findById(messageId)
                .flatMap(message -> {
                    message.setStatus(ChatMessage.MessageStatus.FAILED);
                    message.setCompletedAt(LocalDateTime.now());
                    return chatMessageRepository.save(message);
                })
                .doOnSuccess(saved -> log.debug("실패한 메시지 업데이트 완료: {}", saved.getId()));
    }

    public Flux<ChatMessage> getMessagesByUserAndSession(String userId, String sessionId) {
        return chatMessageRepository.findByUserIdAndSessionIdOrderByCreatedAtDesc(userId, sessionId);
    }

    public Flux<ChatMessage> getMessagesByUser(String userId) {
        return chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Flux<ChatMessage> findByUserIdAndSessionId(String userId, String sessionId) {
        return chatMessageRepository.findByUserIdAndSessionIdOrderByCreatedAtDesc(userId, sessionId);
    }

    public Flux<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}

