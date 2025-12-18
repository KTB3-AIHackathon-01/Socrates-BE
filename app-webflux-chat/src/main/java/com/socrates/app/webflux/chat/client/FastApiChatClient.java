package com.socrates.app.webflux.chat.client;

import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FastApiChatClient {

    Flux<FastApiChatResponse> streamChat(FastApiChatRequest request);

    Mono<FastApiChatResponse> chat(FastApiChatRequest request);
}
