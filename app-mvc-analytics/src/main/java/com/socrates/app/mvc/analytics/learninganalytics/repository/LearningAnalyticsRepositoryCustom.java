package com.socrates.app.mvc.analytics.learninganalytics.repository;

import com.socrates.app.mvc.analytics.learninganalytics.domain.LearningAnalytics;
import com.socrates.app.mvc.analytics.learninganalytics.dto.DailyProgressDto;
import com.socrates.app.mvc.analytics.learninganalytics.dto.QuestionTypeRatioDto;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningAnalyticsRepositoryCustom {

    // 활성 학생 통계
    long countActiveStudents(LocalDateTime startDate, LocalDateTime endDate);

    long countActiveStudentsByStudentIds(List<String> studentIds, LocalDateTime startDate, LocalDateTime endDate);

    long countDistinctStudents();

    // 평균 이해도 통계
    Double getAverageUnderstandingScore();

    Double getAverageUnderstandingScoreByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Double getAverageUnderstandingScoreByStudentIds(List<String> studentIds);

    // 일별 학습 추이
    List<DailyProgressDto> getDailyProgressStats(LocalDateTime startDate, LocalDateTime endDate);

    // 질문 유형 분석
    QuestionTypeRatioDto getAverageQuestionTypeRatio();

    QuestionTypeRatioDto getAverageQuestionTypeRatioByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // 학생별 상세 통계
    List<LearningAnalytics> findLatestAnalyticsByStudentIds(List<String> studentIds);

    Double getStudentAverageUnderstanding(String studentId);

    // 추가 유틸리티
    long countStudentsWhoMasteredConcept(String concept);

    long countStudentsBelowProgressThreshold(double threshold);

    List<LearningAnalytics> findStudentsWithUnresolvedStuckConcepts();
}
