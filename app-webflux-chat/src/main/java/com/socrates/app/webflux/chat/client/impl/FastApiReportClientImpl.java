package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiReportClientImpl implements FastApiReportClient {

    private final WebClient fastapiWebClient;

    @Override
    public Mono<String> generateReport(String sessionId) {
        log.info("FastAPI Report 클라이언트 호출 - sessionId: {}", sessionId);

        return fastapiWebClient.post()
                .uri("/api/report/{sessionId}", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(report -> log.info("FastAPI Report 생성 완료 - sessionId: {}", sessionId))
                .doOnError(error -> log.error("FastAPI Report 생성 실패 - sessionId: {}, error: {}",
                        sessionId, error.getMessage(), error));
    }
}
