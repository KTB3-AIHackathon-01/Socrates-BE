package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiChatClient;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Primary
public class DummyFastApiChatClient implements FastApiChatClient {

    private final Map<String, Integer> sessionCounts = new ConcurrentHashMap<>();

    @Override
    public Flux<FastApiChatResponse> streamChat(ChatRequest request) {
        log.info("더미 FastAPI 클라이언트 호출 - userId: {}, sessionId: {}, message: {}, historyCount: {}",
                request.getUserId(), request.getSessionId(), request.getMessage(),
                request.getHistory() != null ? request.getHistory().size() : 0);

        String sessionId = request.getSessionId();
        int count = sessionCounts.merge(sessionId, 1, Integer::sum);
        boolean isComplete = count >= 5;

        String message = request.getMessage() == null ? "" : request.getMessage();

        FastApiChatResponse response1 = FastApiChatResponse.builder()
                .content("더미 응답입니다. 입력하신 메시지: " + message)
                .isComplete(false)
                .build();

        FastApiChatResponse response2 = FastApiChatResponse.builder()
                .content(" (userId=" + request.getUserId() + ", sessionId=" + request.getSessionId() + ", count=" + count + "/5, history: " +
                        (request.getHistory() != null ? request.getHistory().size() : 0) + ")")
                .isComplete(isComplete)
                .build();

        if (isComplete) {
            sessionCounts.remove(sessionId);
        }

        return Flux.just(response1, response2)
                .delayElements(Duration.ofMillis(300))
                .doOnComplete(() -> log.info("더미 FastAPI 응답 완료 - count: {}/5, isComplete: {}", count, isComplete));
    }

    @Override
    public Mono<FastApiChatResponse> chat(ChatRequest request) {
        log.info("더미 FastAPI Mono 클라이언트 호출 - userId: {}, sessionId: {}, message: {}, historyCount: {}",
                request.getUserId(), request.getSessionId(), request.getMessage(),
                request.getHistory() != null ? request.getHistory().size() : 0);

        String sessionId = request.getSessionId();
        int count = sessionCounts.merge(sessionId, 1, Integer::sum);
        boolean isComplete = count >= 5;

        String message = request.getMessage() == null ? "" : request.getMessage();

        FastApiChatResponse response = FastApiChatResponse.builder()
                .content("더미 응답입니다. 입력하신 메시지: " + message + " (userId=" + request.getUserId() + ", sessionId=" + request.getSessionId() + ", count=" + count + "/5, history: " +
                        (request.getHistory() != null ? request.getHistory().size() : 0) + ")")
                .isComplete(isComplete)
                .build();

        if (isComplete) {
            sessionCounts.remove(sessionId);
        }

        return Mono.just(response)
                .delayElement(Duration.ofMillis(500))
                .doOnSuccess(r -> log.info("더미 FastAPI Mono 응답 완료 - count: {}/5, isComplete: {}", count, r.getIsComplete()));
    }
}

