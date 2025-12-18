package com.socrates.app.webflux.chat.client.impl;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiReportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class DummyFastApiReportClient implements FastApiReportClient {

    @Override
    public Mono<FastApiReportResponse> generateReport(FastApiChatRequest request) {
        List<String> userInput = request.getData() != null && request.getData().getUser_input() != null
                ? request.getData().getUser_input()
                : List.of();

        log.info("더미 FastAPI Report 클라이언트 호출 - inputCount: {}", userInput.size());

        FastApiReportResponse dummyReport = FastApiReportResponse.builder()
                .success(true)
                .report("## 더미 보고서\n- 입력 개수: " + userInput.size() + "\n- 요약: 대화 요약\n- 감정: 긍정적")
                .build();

        return Mono.just(dummyReport)
                .delayElement(Duration.ofMillis(500))
                .doOnSuccess(report -> log.info("더미 FastAPI Report 생성 완료: {}", report));
    }
}
