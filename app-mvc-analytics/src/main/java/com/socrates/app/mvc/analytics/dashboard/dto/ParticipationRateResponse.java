package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRateResponse {

    private Double currentValue;

    private Double growthRate;

    private Double previousValue;

    private Long activeStudents;

    private Long totalStudents;

    public static ParticipationRateResponse of(long currentActive, long previousActive, long total) {
        double currentRate = total > 0 ? (double) currentActive / total : 0.0;
        double previousRate = total > 0 ? (double) previousActive / total : 0.0;

        Double growth = null;
        if (previousRate > 0) {
            growth = (currentRate - previousRate) / previousRate;
        }

        return ParticipationRateResponse.builder()
                .currentValue(currentRate)
                .previousValue(previousRate)
                .growthRate(growth)
                .activeStudents(currentActive)
                .totalStudents(total)
                .build();
    }
}
