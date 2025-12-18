package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConceptMasteryResponse {

    private String concept;

    private Long masteredCount;

    private Long totalStudents;

    private Double masteryRate;

    private Integer masteryPercent;

    public static ConceptMasteryResponse of(String concept, long masteredCount, long totalStudents) {
        double rate = totalStudents > 0 ? (double) masteredCount / totalStudents : 0.0;
        return ConceptMasteryResponse.builder()
                .concept(concept)
                .masteredCount(masteredCount)
                .totalStudents(totalStudents)
                .masteryRate(rate)
                .masteryPercent((int) (rate * 100))
                .build();
    }
}
