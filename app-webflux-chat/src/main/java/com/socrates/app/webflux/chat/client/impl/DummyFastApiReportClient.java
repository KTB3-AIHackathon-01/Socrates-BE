package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@Primary
public class DummyFastApiReportClient implements FastApiReportClient {

    @Override
    public Mono<String> generateReport(String sessionId) {
        log.info("더미 FastAPI Report 클라이언트 호출 - sessionId: {}", sessionId);

        String dummyReport = String.format(
                "{\"sessionId\":\"%s\",\"summary\":\"대화 요약\",\"keyPoints\":[\"포인트1\",\"포인트2\"],\"sentiment\":\"긍정적\"}",
                sessionId
        );

        return Mono.just(dummyReport)
                .delayElement(Duration.ofMillis(500))
                .doOnSuccess(report -> log.info("더미 FastAPI Report 생성 완료: {}", report));
    }
}
