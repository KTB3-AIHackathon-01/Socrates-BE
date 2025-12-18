package com.socrates.app.webflux.chat.client;

import com.socrates.app.webflux.chat.dto.ChatRequest;
import reactor.core.publisher.Flux;

public interface FastApiClient {

    Flux<String> streamChat(ChatRequest request);
}
