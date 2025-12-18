package com.socrates.app.mvc.analytics.learninganalytics.repository;

import com.socrates.app.mvc.analytics.learninganalytics.domain.LearningAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LearningAnalyticsRepository extends MongoRepository<LearningAnalytics, String>,
        LearningAnalyticsRepositoryCustom {

    Optional<LearningAnalytics> findTopByStudentIdOrderByUpdatedAtDesc(String studentId);

    List<LearningAnalytics> findTop2ByStudentIdOrderByUpdatedAtDesc(String studentId);

    Optional<LearningAnalytics> findBySessionId(String sessionId);

    List<LearningAnalytics> findByStudentIdOrderByUpdatedAtDesc(String studentId);
}
