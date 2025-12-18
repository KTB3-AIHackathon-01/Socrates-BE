package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyTrendResponse {

    private String date;

    private String dayOfWeek;

    private Long questionCount;

    private Double understandingScore;

    private Integer understandingPercent;

    public static WeeklyTrendResponse of(String date, String dayOfWeek, Long questionCount, Double understandingScore) {
        return WeeklyTrendResponse.builder()
                .date(date)
                .dayOfWeek(dayOfWeek)
                .questionCount(questionCount != null ? questionCount : 0L)
                .understandingScore(understandingScore)
                .understandingPercent(understandingScore != null ? (int) (understandingScore * 100) : 0)
                .build();
    }
}
