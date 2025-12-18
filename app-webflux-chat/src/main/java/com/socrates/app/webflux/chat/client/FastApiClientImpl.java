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
        log.debug("Calling FastAPI chat stream with request: {}", request);

        return fastapiWebClient.post()
                .uri("/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(data -> log.trace("Received data from FastAPI: {}", data))
                .doOnError(error -> log.error("FastAPI error: {}", error.getMessage(), error))
                .doOnComplete(() -> log.debug("FastAPI stream completed"));
    }
}
