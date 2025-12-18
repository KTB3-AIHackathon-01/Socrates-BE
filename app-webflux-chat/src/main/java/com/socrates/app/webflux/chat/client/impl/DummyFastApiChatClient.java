package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiChatClient;
import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Primary
public class DummyFastApiChatClient implements FastApiChatClient {

    private final Map<String, Integer> sessionCounts = new ConcurrentHashMap<>();

    @Override
    public Flux<FastApiChatResponse> streamChat(FastApiChatRequest request) {
        List<String> userInput = request.getData() != null && request.getData().getUser_input() != null
                ? request.getData().getUser_input()
                : List.of();

        String lastMessage = userInput.isEmpty() ? "" : userInput.get(userInput.size() - 1);

        log.info("더미 FastAPI 클라이언트 호출 - message: {}, inputCount: {}",
                lastMessage, userInput.size());

        String sessionKey = String.valueOf(userInput.hashCode());
        int count = sessionCounts.merge(sessionKey, 1, Integer::sum);
        boolean isComplete = count >= 5;

        FastApiChatResponse response1 = FastApiChatResponse.builder()
                .success(true)
                .isCompleted(false)
                .data(FastApiChatResponse.ResponseData.builder()
                        .userFacingMessage("더미 응답입니다. 입력하신 메시지: " + lastMessage)
                        .isStuck(false)
                        .nextAction("")
                        .build())
                .build();

        FastApiChatResponse response2 = FastApiChatResponse.builder()
                .success(true)
                .isCompleted(isComplete)
                .data(FastApiChatResponse.ResponseData.builder()
                        .userFacingMessage(" (count=" + count + "/5, inputCount: " + userInput.size() + ")")
                        .isStuck(count >= 3)
                        .nextAction("")
                        .build())
                .build();

        if (isComplete) {
            sessionCounts.remove(sessionKey);
        }

        return Flux.just(response1, response2)
                .delayElements(Duration.ofMillis(300))
                .doOnComplete(() -> log.info("더미 FastAPI 응답 완료 - count: {}/5, isComplete: {}", count, isComplete));
    }

    @Override
    public Mono<FastApiChatResponse> chat(FastApiChatRequest request) {
        List<String> userInput = request.getData() != null && request.getData().getUser_input() != null
                ? request.getData().getUser_input()
                : List.of();

        String lastMessage = userInput.isEmpty() ? "" : userInput.get(userInput.size() - 1);

        log.info("더미 FastAPI Mono 클라이언트 호출 - message: {}, inputCount: {}",
                lastMessage, userInput.size());

        String sessionKey = String.valueOf(userInput.hashCode());
        int count = sessionCounts.merge(sessionKey, 1, Integer::sum);
        boolean isComplete = count >= 5;

        FastApiChatResponse response = FastApiChatResponse.builder()
                .success(true)
                .isCompleted(isComplete)
                .data(FastApiChatResponse.ResponseData.builder()
                        .userFacingMessage("더미 응답입니다. 입력하신 메시지: " + lastMessage + " (count=" + count + "/5, inputCount: " + userInput.size() + ")")
                        .isStuck(count >= 3)
                        .nextAction("")
                        .build())
                .build();

        if (isComplete) {
            sessionCounts.remove(sessionKey);
        }

        return Mono.just(response)
                .delayElement(Duration.ofMillis(500))
                .doOnSuccess(r -> log.info("더미 FastAPI Mono 응답 완료 - count: {}/5, isCompleted: {}", count, r.getIsCompleted()));
    }
}

