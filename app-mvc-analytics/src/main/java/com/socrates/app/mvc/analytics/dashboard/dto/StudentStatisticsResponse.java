package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatisticsResponse {

    private String studentId;

    private String studentName;

    private Long questionCount;

    private Double understandingScore;

    private Integer understandingPercent;

    private String understandingLevel;

    private LocalDateTime lastActivity;

    private String lastActivityFormatted;

    private String trend;

    private String trendIcon;

    public static String calculateUnderstandingLevel(Double score) {
        if (score == null) return "없음";
        if (score >= 0.8) return "높음";
        if (score >= 0.5) return "보통";
        return "낮음";
    }

    public static String calculateTrend(Double current, Double previous) {
        if (current == null || previous == null) return "stable";
        double diff = current - previous;
        if (diff > 0.05) return "increasing";
        if (diff < -0.05) return "decreasing";
        return "stable";
    }

    public static String getTrendIcon(String trend) {
        return switch (trend) {
            case "increasing" -> "↑상승";
            case "decreasing" -> "↓하락";
            default -> "→유지";
        };
    }
}
