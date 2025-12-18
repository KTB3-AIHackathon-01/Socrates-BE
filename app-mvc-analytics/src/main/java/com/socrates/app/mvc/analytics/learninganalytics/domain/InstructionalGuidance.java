package com.socrates.app.mvc.analytics.learninganalytics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructionalGuidance {

    private List<String> nextFocusConcepts;

    private List<String> teachingRecommendations;

    private String nextSessionGoal;

    private String recommendedPractice;
}
