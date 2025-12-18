package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class FastApiReportClientImpl implements FastApiReportClient {

    private final WebClient fastapiWebClient;

    @Override
    public Mono<FastApiReportResponse> generateReport(FastApiChatRequest request) {
        log.info("FastAPI Report 클라이언트 호출 - request: {}", request);

        return fastapiWebClient.post()
                .uri("/chat/report")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FastApiReportResponse.class)
                .doOnSuccess(report -> log.info("FastAPI Report 생성 완료"))
                .doOnError(error -> log.error("FastAPI Report 생성 실패 - error: {}", error.getMessage(), error));
    }
}
