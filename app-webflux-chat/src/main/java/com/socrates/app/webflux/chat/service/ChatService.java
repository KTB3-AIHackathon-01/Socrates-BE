package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.client.FastApiClient;
import com.socrates.app.webflux.chat.domain.ChatMessage;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final FastApiClient fastApiClient;
    private final ChatMessageRepository chatMessageRepository;

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
        log.info("사용자 채팅 스트림 처리 시작: {}", request.getUserId());

        return savePendingMessage(request)
                .flatMapMany(savedMessage ->
                        fastApiClient.streamChat(request)
                                .collectList()
                                .flatMapMany(chunks -> {
                                    String fullResponse = String.join("", chunks);
                                    return updateCompletedMessage(savedMessage.getId(), fullResponse)
                                            .thenMany(Flux.fromIterable(chunks));
                                })
                                .map(data -> ServerSentEvent.<String>builder()
                                        .data(data)
                                        .build())
                                .doOnNext(sse -> log.debug("응답 스트림 전송: {}", sse.data()))
                                .doOnError(error -> {
                                    log.error("채팅 스트림 오류 발생: {}", error.getMessage());
                                    updateFailedMessage(savedMessage.getId()).subscribe();
                                })
                                .doOnComplete(() -> log.info("사용자 채팅 스트림 처리 완료: {}", request.getUserId()))
                );
    }

    private Mono<ChatMessage> savePendingMessage(ChatRequest request) {
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

    private Mono<ChatMessage> updateCompletedMessage(String messageId, String assistantMessage) {
        return chatMessageRepository.findById(messageId)
                .flatMap(message -> {
                    message.setAssistantMessage(assistantMessage);
                    message.setCompletedAt(LocalDateTime.now());
                    message.setStatus(ChatMessage.MessageStatus.COMPLETED);
                    return chatMessageRepository.save(message);
                })
                .doOnSuccess(saved -> log.debug("완료된 메시지 업데이트 완료: {}", saved.getId()));
    }

    private Mono<ChatMessage> updateFailedMessage(String messageId) {
        return chatMessageRepository.findById(messageId)
                .flatMap(message -> {
                    message.setStatus(ChatMessage.MessageStatus.FAILED);
                    message.setCompletedAt(LocalDateTime.now());
                    return chatMessageRepository.save(message);
                })
                .doOnSuccess(saved -> log.debug("실패한 메시지 업데이트 완료: {}", saved.getId()));
    }
}
