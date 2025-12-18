package com.socrates.app.mvc.analytics.learninganalytics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StuckConcept {

    private String concept;

    private Integer stuckTurns;

    private Boolean resolved;
}
