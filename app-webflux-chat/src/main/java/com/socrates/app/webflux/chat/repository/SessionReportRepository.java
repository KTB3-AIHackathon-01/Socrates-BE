package com.socrates.app.webflux.chat.repository;

import com.socrates.app.webflux.chat.domain.SessionReport;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionReportRepository extends ReactiveMongoRepository<SessionReport, String> {

    Mono<SessionReport> findBySessionId(String sessionId);

    Flux<SessionReport> findByUserIdOrderByCreatedAtDesc(String userId);
}
