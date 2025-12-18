package com.socrates.app.webflux.chat.client;

import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiReportResponse;
import reactor.core.publisher.Mono;

public interface FastApiReportClient {

    Mono<FastApiReportResponse> generateReport(FastApiChatRequest request);
}
