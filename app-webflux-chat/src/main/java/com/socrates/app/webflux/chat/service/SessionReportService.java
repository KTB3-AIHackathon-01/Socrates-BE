package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.client.FastApiReportClient;
import com.socrates.app.webflux.chat.domain.SessionReport;
import com.socrates.app.webflux.chat.repository.SessionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionReportService {

    private final FastApiReportClient fastApiReportClient;
    private final SessionReportRepository sessionReportRepository;

    public Mono<Void> generateReportAsync(String userId, String sessionId) {
        log.info("비동기 리포트 생성 시작 - userId: {}, sessionId: {}", userId, sessionId);

        return createPendingReport(userId, sessionId)
                .flatMap(report -> fastApiReportClient.generateReport(sessionId)
                        .flatMap(reportData -> updateCompletedReport(report.getId(), reportData))
                        .onErrorResume(error -> {
                            log.error("리포트 생성 실패 - sessionId: {}, error: {}", sessionId, error.getMessage());
                            return updateFailedReport(report.getId());
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(v -> log.info("비동기 리포트 생성 완료 - sessionId: {}", sessionId))
                .then();
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

    private Mono<SessionReport> updateCompletedReport(String reportId, String reportData) {
        return sessionReportRepository.findById(reportId)
                .flatMap(report -> {
                    report.setReportData(reportData);
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

    public Mono<SessionReport> getReportBySessionId(String sessionId) {
        return sessionReportRepository.findBySessionId(sessionId);
    }
}
