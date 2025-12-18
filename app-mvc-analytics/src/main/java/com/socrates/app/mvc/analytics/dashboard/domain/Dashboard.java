package com.socrates.app.mvc.analytics.dashboard.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "dashboards")
public class Dashboard {

    @Id
    private String id;

    private String studentId;

    private LearningSummary learningSummary;

    private List<ConceptMastery> conceptMastery;

    private LearningDifficulty learningDifficulty;

    private LearningBehavior learningBehavior;

    private InstructionalGuidance instructionalGuidance;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LearningSummary {
        private Double overallProgressScore;
        private Double overallDifficultyScore;
        private Double masteryRatio;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ConceptMastery {
        private String concept;
        private String importance; // "high" | "medium" | "low"
        private String status; // "not_started" | "partial" | "mastered"
        private Double understandingScore;
        private Boolean breakthrough;
        private String evidenceQuestion; // nullable
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LearningDifficulty {
        private String primaryRootCause; // "abstract_to_concrete" | "terminology_focus" | "process_confusion" | "application_gap" | "math_gap"
        private String secondaryRootCause; // same as primary | null
        private List<StuckConcept> stuckConcepts;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class StuckConcept {
        private String concept;
        private Integer stuckTurns;
        private Boolean resolved;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LearningBehavior {
        private Double questionDepthScore;
        private QuestionTypeRatio questionTypeRatio;
        private Double conceptLinkScore;
        private Double confirmationQuestionRatio;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class QuestionTypeRatio {
        private Double definition;
        private Double mechanism;
        private Double comparison;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class InstructionalGuidance {
        private List<String> nextFocusConcepts;
        private List<String> teachingRecommendations;
        private String nextSessionGoal;
        private String recommendedPractice;
    }
}

