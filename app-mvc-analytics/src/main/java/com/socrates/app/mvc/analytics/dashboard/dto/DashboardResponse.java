package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private MetricsSection metrics;

    private List<WeeklyTrendResponse> weeklyTrend;

    private QuestionTypeResponse questionTypes;

    private List<StudentStatisticsResponse> studentStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricsSection {
        private MetricResponse activeStudents;
        private MetricResponse todayQuestions;
        private MetricResponse averageUnderstanding;
        private ParticipationRateResponse learningParticipation;
    }
}
