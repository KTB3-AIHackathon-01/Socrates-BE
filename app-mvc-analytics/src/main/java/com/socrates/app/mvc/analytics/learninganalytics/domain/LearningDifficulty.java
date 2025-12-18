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
public class LearningDifficulty {

    private RootCause primaryRootCause;

    private RootCause secondaryRootCause;

    private List<StuckConcept> stuckConcepts;
}
