package com.socrates.app.mvc.analytics.learninganalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTypeRatioDto {

    private Double avgDefinition;

    private Double avgMechanism;

    private Double avgComparison;

    public Double getAvgReview() {
        double total = (avgDefinition != null ? avgDefinition : 0.0)
                + (avgMechanism != null ? avgMechanism : 0.0)
                + (avgComparison != null ? avgComparison : 0.0);
        return Math.max(0.0, 1.0 - total);
    }
}
