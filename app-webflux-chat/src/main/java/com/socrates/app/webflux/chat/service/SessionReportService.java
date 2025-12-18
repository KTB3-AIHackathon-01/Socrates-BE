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
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionReportService {

    private final FastApiReportClient fastApiReportClient;
    private final SessionReportRepository sessionReportRepository;
    private final ChatMessageService chatMessageService;

    public Mono<SessionReport> generateReport(String sessionId) {
        log.info("리포트 생성 시작 - sessionId: {}", sessionId);

        return sessionReportRepository.findBySessionId(sessionId)
                .flatMap(existingReport -> {
                    if (existingReport.getStatus() == SessionReport.ReportStatus.COMPLETED) {
                        log.info("이미 완료된 리포트 존재 - sessionId: {}", sessionId);
                        return Mono.just(existingReport);
                    }
                    return regenerateReport(sessionId, existingReport);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("신규 리포트 생성 - sessionId: {}", sessionId);
                    return getUserIdFromMessages(sessionId)
                            .flatMap(userId -> createPendingReport(userId, sessionId)
                                    .flatMap(report -> regenerateReport(sessionId, report)));
                }))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<String> getUserIdFromMessages(String sessionId) {
        return chatMessageService.getChatHistory(sessionId)
                .next()
                .map(ChatMessage::getUserId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당 세션에 메시지가 없습니다: " + sessionId)));
    }

    private Mono<SessionReport> regenerateReport(String sessionId, SessionReport report) {
        return buildReportRequest(sessionId)
                .flatMap(request -> fastApiReportClient.generateReport(request)
                        .flatMap(reportResponse -> updateCompletedReport(report.getId(), reportResponse))
                        .onErrorResume(error -> {
                            log.error("리포트 생성 실패 - sessionId: {}, error: {}", sessionId, error.getMessage());
                            return updateFailedReport(report.getId());
                        })
                );
    }

    private Mono<FastApiChatRequest> buildReportRequest(String sessionId) {
        return chatMessageService.getChatHistory(sessionId)
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

}
