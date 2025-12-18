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

@Slf4j
@Component
@Primary
public class DummyFastApiChatClient implements FastApiChatClient {

    private int callCount = 0;

    @Override
    public Flux<FastApiChatResponse> streamChat(ChatRequest request) {
        log.info("더미 FastAPI 클라이언트 호출 - userId: {}, sessionId: {}, message: {}",
                request.getUserId(), request.getSessionId(), request.getMessage());

        callCount++;
        String message = request.getMessage() == null ? "" : request.getMessage();

        FastApiChatResponse response1 = FastApiChatResponse.builder()
                .content("더미 응답입니다. 입력하신 메시지: " + message)
                .isComplete(false)
                .build();

        FastApiChatResponse response2 = FastApiChatResponse.builder()
                .content(" (userId=" + request.getUserId() + ", sessionId=" + request.getSessionId() + ")")
                .isComplete(callCount % 3 == 0)
                .build();

        return Flux.just(response1, response2)
                .delayElements(Duration.ofMillis(300))
                .doOnComplete(() -> log.info("더미 FastAPI 응답 완료 - isComplete: {}", response2.getIsComplete()));
    }

    @Override
    public Mono<FastApiChatResponse> chat(ChatRequest request) {
        log.info("더미 FastAPI Mono 클라이언트 호출 - userId: {}, sessionId: {}, message: {}",
                request.getUserId(), request.getSessionId(), request.getMessage());

        callCount++;
        String message = request.getMessage() == null ? "" : request.getMessage();

        FastApiChatResponse response = FastApiChatResponse.builder()
                .content("더미 응답입니다. 입력하신 메시지: " + message + " (userId=" + request.getUserId() + ", sessionId=" + request.getSessionId() + ")")
                .isComplete(callCount % 3 == 0)
                .build();

        return Mono.just(response)
                .delayElement(Duration.ofMillis(500))
                .doOnSuccess(r -> log.info("더미 FastAPI Mono 응답 완료 - isComplete: {}", r.getIsComplete()));
    }
}

