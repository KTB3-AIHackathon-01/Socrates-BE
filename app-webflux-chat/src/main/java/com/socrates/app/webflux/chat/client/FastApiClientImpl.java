package com.socrates.app.webflux.chat.client;

import com.socrates.app.webflux.chat.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiClientImpl implements FastApiClient {

    private final WebClient fastapiWebClient;

    @Override
    public Flux<String> streamChat(ChatRequest request) {
        log.debug("FastAPI 채팅 스트림 호출 시작, 요청: {}", request);

        return fastapiWebClient.post()
                .uri("/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(data -> log.trace("FastAPI로부터 데이터 수신: {}", data))
                .doOnError(error -> log.error("FastAPI 오류 발생: {}", error.getMessage(), error))
                .doOnComplete(() -> log.debug("FastAPI 스트림 종료"));
    }
}
