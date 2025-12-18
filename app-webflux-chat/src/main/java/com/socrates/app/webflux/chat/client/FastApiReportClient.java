package com.socrates.app.webflux.chat.client;

import reactor.core.publisher.Mono;

public interface FastApiReportClient {

    Mono<String> generateReport(String sessionId);
}
