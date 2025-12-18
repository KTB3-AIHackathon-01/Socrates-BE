package com.socrates.app.webflux.chat.client;

import com.socrates.app.webflux.chat.dto.ChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Component
@Primary
public class DummyFastApiClient implements FastApiClient {

    @Override
    public Flux<String> streamChat(ChatRequest request) {
        log.info("더미 FastAPI 클라이언트 호출 - userId: {}, sessionId: {}, message: {}",
                request.getUserId(), request.getSessionId(), request.getMessage());

        String message = request.getMessage() == null ? "" : request.getMessage();

        return Flux.just(
                        "더미 응답입니다. 입력하신 메시지: " + message,
                        " (userId=" + request.getUserId() + ", sessionId=" + request.getSessionId() + ")"
                )
                .delayElements(Duration.ofMillis(300))
                .doOnComplete(() -> log.info("더미 FastAPI 응답 완료"));
    }
}

