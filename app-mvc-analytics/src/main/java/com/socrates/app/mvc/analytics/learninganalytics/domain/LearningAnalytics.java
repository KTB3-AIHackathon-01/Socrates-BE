package com.socrates.app.mvc.analytics.learninganalytics.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "learning_analytics")
public class LearningAnalytics {

    @Id
    private String id;

    private String studentId;

    private String sessionId;

    private LearningSummary learningSummary;

    private List<ConceptMastery> conceptMastery;

    private LearningDifficulty learningDifficulty;

    private LearningBehavior learningBehavior;

    private InstructionalGuidance instructionalGuidance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
