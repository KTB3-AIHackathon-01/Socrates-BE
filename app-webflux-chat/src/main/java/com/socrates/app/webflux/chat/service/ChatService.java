package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final WebClient fastapiWebClient;

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
        log.debug("Streaming chat request: {}", request);

        return fastapiWebClient.post()
                .uri("/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .map(data -> ServerSentEvent.<String>builder()
                        .data(data)
                        .build())
                .doOnNext(sse -> log.trace("Received SSE: {}", sse.data()))
                .doOnError(error -> log.error("Error streaming chat: {}", error.getMessage()))
                .doOnComplete(() -> log.debug("Chat stream completed"));
    }
}
