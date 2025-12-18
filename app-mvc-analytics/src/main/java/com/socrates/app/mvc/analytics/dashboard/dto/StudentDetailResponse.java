package com.socrates.app.mvc.analytics.dashboard.dto;

import com.socrates.app.mvc.analytics.learninganalytics.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailResponse {

    private String studentId;

    private String studentName;

    private Long totalQuestions;

    private LocalDateTime lastActivity;

    private LearningSummary learningSummary;

    private List<ConceptMastery> conceptMastery;

    private LearningDifficulty learningDifficulty;

    private LearningBehavior learningBehavior;

    private InstructionalGuidance instructionalGuidance;

    private String trend;

    public static StudentDetailResponse from(LearningAnalytics analytics, String studentName,
                                              Long questionCount, String trend) {
        return StudentDetailResponse.builder()
                .studentId(analytics.getStudentId())
                .studentName(studentName)
                .totalQuestions(questionCount)
                .lastActivity(analytics.getUpdatedAt())
                .learningSummary(analytics.getLearningSummary())
                .conceptMastery(analytics.getConceptMastery())
                .learningDifficulty(analytics.getLearningDifficulty())
                .learningBehavior(analytics.getLearningBehavior())
                .instructionalGuidance(analytics.getInstructionalGuidance())
                .trend(trend)
                .build();
    }
}
