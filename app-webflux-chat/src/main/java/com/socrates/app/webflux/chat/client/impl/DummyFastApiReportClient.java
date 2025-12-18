package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import com.socrates.app.webflux.chat.dto.FastApiReportResponse;
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
    public Mono<FastApiReportResponse> generateReport(String sessionId) {
        log.info("더미 FastAPI Report 클라이언트 호출 - sessionId: {}", sessionId);

        FastApiReportResponse dummyReport = FastApiReportResponse.builder()
                .markdown("## 더미 보고서\n- 세션: " + sessionId + "\n- 요약: 대화 요약\n- 감정: 긍정적")
                .json(String.format(
                        "{\"sessionId\":\"%s\",\"summary\":\"대화 요약\",\"keyPoints\":[\"포인트1\",\"포인트2\"],\"sentiment\":\"긍정적\"}",
                        sessionId))
                .build();

        return Mono.just(dummyReport)
                .delayElement(Duration.ofMillis(500))
                .doOnSuccess(report -> log.info("더미 FastAPI Report 생성 완료: {}", report));
    }
}
