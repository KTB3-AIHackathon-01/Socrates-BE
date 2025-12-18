package com.socrates.app.mvc.analytics.learninganalytics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSummary {

    private Double overallProgressScore;

    private Double overallDifficultyScore;

    private Double masteryRatio;
}
