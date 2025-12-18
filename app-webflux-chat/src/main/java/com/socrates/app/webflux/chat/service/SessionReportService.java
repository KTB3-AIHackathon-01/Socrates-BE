package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import com.socrates.app.webflux.chat.domain.ChatMessage;
import com.socrates.app.webflux.chat.domain.SessionReport;
import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiReportResponse;
import com.socrates.app.webflux.chat.repository.SessionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionReportService {

    private final FastApiReportClient fastApiReportClient;
    private final SessionReportRepository sessionReportRepository;
    private final ChatMessageService chatMessageService;
    private final Map<String, Sinks.Many<String>> reportSinks = new ConcurrentHashMap<>();

    public Mono<Void> generateReportAsync(String userId, String sessionId) {
        log.info("비동기 리포트 생성 시작 - userId: {}, sessionId: {}", userId, sessionId);

        return createPendingReport(userId, sessionId)
                .flatMap(report -> buildReportRequest(userId, sessionId)
                        .flatMap(request -> fastApiReportClient.generateReport(request)
                                .flatMap(reportResponse -> updateCompletedReport(report.getId(), reportResponse)
                                        .doOnSuccess(savedReport -> emitReportMarkdown(sessionId, reportResponse.getReport()))
                                )
                                .onErrorResume(error -> {
                                    log.error("리포트 생성 실패 - sessionId: {}, error: {}", sessionId, error.getMessage());
                                    return updateFailedReport(report.getId());
                                })
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(v -> log.info("비동기 리포트 생성 완료 - sessionId: {}", sessionId))
                .doFinally(signal -> cleanupSink(sessionId))
                .then();
    }

    private Mono<FastApiChatRequest> buildReportRequest(String userId, String sessionId) {
        return chatMessageService.findByUserIdAndSessionId(userId, sessionId)
                .filter(msg -> msg.getStatus() == ChatMessage.MessageStatus.COMPLETED)
                .collectList()
                .map(messages -> {
                    List<String> userInputList = new ArrayList<>();
                    for (ChatMessage msg : messages) {
                        userInputList.add(msg.getUserMessage());
                        userInputList.add(msg.getAssistantMessage());
                    }

                    FastApiChatRequest.DataWrapper dataWrapper = FastApiChatRequest.DataWrapper.builder()
                            .user_input(userInputList)
                            .build();

                    return FastApiChatRequest.builder()
                            .data(dataWrapper)
                            .build();
                })
                .doOnSuccess(request -> log.debug("리포트 요청 생성 완료 - sessionId: {}, messageCount: {}",
                        sessionId, request.getData().getUser_input().size()));
    }

    private Mono<SessionReport> createPendingReport(String userId, String sessionId) {
        SessionReport report = SessionReport.builder()
                .userId(userId)
                .sessionId(sessionId)
                .createdAt(LocalDateTime.now())
                .status(SessionReport.ReportStatus.PENDING)
                .build();

        return sessionReportRepository.save(report)
                .doOnSuccess(saved -> log.debug("대기 중 리포트 생성: {}", saved.getId()));
    }

    private Mono<SessionReport> updateCompletedReport(String reportId, FastApiReportResponse reportResponse) {
        return sessionReportRepository.findById(reportId)
                .flatMap(report -> {
                    report.setReportData(reportResponse.getReport());
                    report.setReportJson(null);
                    report.setStatus(SessionReport.ReportStatus.COMPLETED);
                    return sessionReportRepository.save(report);
                })
                .doOnSuccess(saved -> log.debug("리포트 완료 업데이트: {}", saved.getId()));
    }

    private Mono<SessionReport> updateFailedReport(String reportId) {
        return sessionReportRepository.findById(reportId)
                .flatMap(report -> {
                    report.setStatus(SessionReport.ReportStatus.FAILED);
                    return sessionReportRepository.save(report);
                })
                .doOnSuccess(saved -> log.debug("리포트 실패 업데이트: {}", saved.getId()));
    }

    private void emitReportMarkdown(String sessionId, String markdown) {
        Sinks.Many<String> sink = reportSinks.get(sessionId);
        if (sink != null) {
            log.debug("리포트 Markdown 전송 - sessionId: {}", sessionId);
            sink.tryEmitNext(markdown);
            sink.tryEmitComplete();
        }
    }

    private void cleanupSink(String sessionId) {
        reportSinks.remove(sessionId);
        log.debug("리포트 Sink 정리 - sessionId: {}", sessionId);
    }

    public void registerReportSink(String sessionId, Sinks.Many<String> sink) {
        reportSinks.put(sessionId, sink);
        log.debug("리포트 Sink 등록 - sessionId: {}", sessionId);
    }

    public Mono<SessionReport> getReportBySessionId(String sessionId) {
        return sessionReportRepository.findBySessionId(sessionId);
    }
}
