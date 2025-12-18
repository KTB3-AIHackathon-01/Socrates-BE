package com.socrates.app.webflux.chat.client;

import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FastApiChatClient {

    Flux<FastApiChatResponse> streamChat(ChatRequest request);

    Mono<FastApiChatResponse> chat(ChatRequest request);
}
