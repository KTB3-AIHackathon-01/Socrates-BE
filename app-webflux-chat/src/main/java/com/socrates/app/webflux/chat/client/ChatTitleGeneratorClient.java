package com.socrates.app.webflux.chat.client;

import reactor.core.publisher.Mono;

public interface ChatTitleGeneratorClient {

    Mono<String> generateTitle(String firstMessage);
}
