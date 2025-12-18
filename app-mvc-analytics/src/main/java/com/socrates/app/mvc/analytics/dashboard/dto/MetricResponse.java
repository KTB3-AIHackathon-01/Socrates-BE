package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponse {

    private Number currentValue;

    private Double growthRate;

    private Number previousValue;

    public static MetricResponse of(Number current, Number previous) {
        double currentDouble = current != null ? current.doubleValue() : 0.0;
        double previousDouble = previous != null ? previous.doubleValue() : 0.0;

        Double growth = null;
        if (previousDouble > 0) {
            growth = (currentDouble - previousDouble) / previousDouble;
        }

        return MetricResponse.builder()
                .currentValue(current)
                .previousValue(previous)
                .growthRate(growth)
                .build();
    }
}
