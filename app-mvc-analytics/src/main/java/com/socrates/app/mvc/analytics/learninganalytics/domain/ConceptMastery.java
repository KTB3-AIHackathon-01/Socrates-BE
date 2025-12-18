package com.socrates.app.mvc.analytics.learninganalytics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConceptMastery {

    private String concept;

    private Importance importance;

    private MasteryStatus status;

    private Double understandingScore;

    private Boolean breakthrough;

    private String evidenceQuestion;
}
