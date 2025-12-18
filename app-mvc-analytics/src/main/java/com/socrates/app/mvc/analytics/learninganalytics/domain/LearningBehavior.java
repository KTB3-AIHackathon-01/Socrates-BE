package com.socrates.app.mvc.analytics.learninganalytics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningBehavior {

    private Double questionDepthScore;

    private QuestionTypeRatio questionTypeRatio;

    private Double conceptLinkScore;

    private Double confirmationQuestionRatio;
}
