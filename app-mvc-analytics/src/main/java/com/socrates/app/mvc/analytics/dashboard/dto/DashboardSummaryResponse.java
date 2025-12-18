package com.socrates.app.mvc.analytics.dashboard.dto;

import com.socrates.app.mvc.analytics.dashboard.domain.Dashboard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Schema(description = "학습 대시보드 요약 정보")
public record DashboardSummaryResponse(
        @Schema(description = "학생 ID")
        String studentId,

        @Schema(description = "상단 요약 카드")
        SummaryResponse summary,

        @Schema(description = "개념별 이해도")
        List<ConceptResponse> concepts,

        @Schema(description = "개입 필요 신호")
        LearningAlertsResponse learningAlerts,

        @Schema(description = "학습 행동 인사이트")
        LearningInsightsResponse learningInsights,

        @Schema(description = "다음 학습 지도 가이드")
        NextGuidanceResponse nextGuidance
) {
    public static DashboardSummaryResponse from(Dashboard dashboard) {
        return DashboardSummaryResponse.builder()
                .studentId(dashboard.getStudentId())
                .summary(SummaryResponse.from(dashboard))
                .concepts(ConceptResponse.fromList(dashboard.getConceptMastery()))
                .learningAlerts(LearningAlertsResponse.from(dashboard))
                .learningInsights(LearningInsightsResponse.from(dashboard))
                .nextGuidance(NextGuidanceResponse.from(dashboard))
                .build();
    }

    @Builder
    @Schema(description = "상단 요약 카드")
    public record SummaryResponse(
            @Schema(description = "전체 학습 진행 상태")
            ProgressResponse progress,

            @Schema(description = "강사용 요약 문장")
            String statusMessage
    ) {
        public static SummaryResponse from(Dashboard dashboard) {
            Dashboard.LearningSummary learningSummary = dashboard.getLearningSummary();
            if (learningSummary == null) {
                return SummaryResponse.builder()
                        .progress(ProgressResponse.builder()
                                .progressScore(0.0)
                                .difficultyScore(0.0)
                                .masteryRatio(0.0)
                                .build())
                        .statusMessage("학습 데이터가 없습니다")
                        .build();
            }

            ProgressResponse progress = ProgressResponse.builder()
                    .progressScore(learningSummary.getOverallProgressScore() != null
                            ? learningSummary.getOverallProgressScore()
                            : 0.0)
                    .difficultyScore(learningSummary.getOverallDifficultyScore() != null
                            ? learningSummary.getOverallDifficultyScore()
                            : 0.0)
                    .masteryRatio(learningSummary.getMasteryRatio() != null
                            ? learningSummary.getMasteryRatio()
                            : 0.0)
                    .build();

            String statusMessage = generateStatusMessage(dashboard);

            return SummaryResponse.builder()
                    .progress(progress)
                    .statusMessage(statusMessage)
                    .build();
        }

        private static String generateStatusMessage(Dashboard dashboard) {
            List<Dashboard.ConceptMastery> conceptMastery = dashboard.getConceptMastery();
            if (conceptMastery == null || conceptMastery.isEmpty()) {
                return "학습 데이터가 없습니다";
            }

            List<String> masteredConcepts = conceptMastery.stream()
                    .filter(c -> "mastered".equals(c.getStatus()))
                    .map(Dashboard.ConceptMastery::getConcept)
                    .collect(Collectors.toList());

            List<String> notStartedConcepts = conceptMastery.stream()
                    .filter(c -> "not_started".equals(c.getStatus()))
                    .map(Dashboard.ConceptMastery::getConcept)
                    .collect(Collectors.toList());

            if (!masteredConcepts.isEmpty() && !notStartedConcepts.isEmpty()) {
                return String.format("핵심 개념(%s)은 이해했으나, %s 개념은 아직 미학습 상태",
                        String.join(", ", masteredConcepts),
                        String.join(", ", notStartedConcepts));
            } else if (!masteredConcepts.isEmpty()) {
                return String.format("핵심 개념(%s)은 이해했습니다",
                        String.join(", ", masteredConcepts));
            } else if (!notStartedConcepts.isEmpty()) {
                return String.format("%s 개념은 아직 미학습 상태입니다",
                        String.join(", ", notStartedConcepts));
            } else {
                return "학습이 진행 중입니다";
            }
        }
    }

    @Builder
    @Schema(description = "전체 학습 진행 상태")
    public record ProgressResponse(
            @Schema(description = "진행 점수 (0.0 ~ 1.0)")
            Double progressScore,

            @Schema(description = "난이도 점수 (0.0 ~ 1.0)")
            Double difficultyScore,

            @Schema(description = "숙달 비율 (0.0 ~ 1.0)")
            Double masteryRatio
    ) {
    }

    @Builder
    @Schema(description = "개념별 이해도")
    public record ConceptResponse(
            @Schema(description = "개념")
            String concept,

            @Schema(description = "이해 상태", example = "mastered")
            String status,

            @Schema(description = "정량 이해도 (0.0 ~ 1.0)")
            Double understandingScore,

            @Schema(description = "학습 전환 포인트 여부")
            Boolean breakthrough,

            @Schema(description = "근거 질문", nullable = true)
            String evidenceQuestion
    ) {
        public static List<ConceptResponse> fromList(List<Dashboard.ConceptMastery> conceptMasteryList) {
            if (conceptMasteryList == null) {
                return List.of();
            }
            return conceptMasteryList.stream()
                    .map(ConceptResponse::from)
                    .collect(Collectors.toList());
        }

        public static ConceptResponse from(Dashboard.ConceptMastery conceptMastery) {
            return ConceptResponse.builder()
                    .concept(conceptMastery.getConcept())
                    .status(conceptMastery.getStatus())
                    .understandingScore(conceptMastery.getUnderstandingScore() != null
                            ? conceptMastery.getUnderstandingScore()
                            : 0.0)
                    .breakthrough(conceptMastery.getBreakthrough() != null
                            ? conceptMastery.getBreakthrough()
                            : false)
                    .evidenceQuestion(conceptMastery.getEvidenceQuestion())
                    .build();
        }
    }

    @Builder
    @Schema(description = "개입 필요 신호")
    public record LearningAlertsResponse(
            @Schema(description = "가장 주요한 학습 장애 원인")
            PrimaryDifficultyResponse primaryDifficulty,

            @Schema(description = "막힘이 관찰된 개념 요약")
            List<StuckConceptSummaryResponse> stuckConcepts
    ) {
        public static LearningAlertsResponse from(Dashboard dashboard) {
            Dashboard.LearningDifficulty learningDifficulty = dashboard.getLearningDifficulty();
            if (learningDifficulty == null) {
                return LearningAlertsResponse.builder()
                        .primaryDifficulty(null)
                        .stuckConcepts(List.of())
                        .build();
            }

            PrimaryDifficultyResponse primaryDifficulty = null;
            if (learningDifficulty.getPrimaryRootCause() != null) {
                primaryDifficulty = PrimaryDifficultyResponse.builder()
                        .type(learningDifficulty.getPrimaryRootCause())
                        .label(getDifficultyLabel(learningDifficulty.getPrimaryRootCause()))
                        .build();
            }

            List<StuckConceptSummaryResponse> stuckConcepts = List.of();
            if (learningDifficulty.getStuckConcepts() != null) {
                stuckConcepts = learningDifficulty.getStuckConcepts().stream()
                        .map(sc -> StuckConceptSummaryResponse.builder()
                                .concept(sc.getConcept())
                                .resolved(sc.getResolved() != null ? sc.getResolved() : false)
                                .build())
                        .collect(Collectors.toList());
            }

            return LearningAlertsResponse.builder()
                    .primaryDifficulty(primaryDifficulty)
                    .stuckConcepts(stuckConcepts)
                    .build();
        }

        private static String getDifficultyLabel(String type) {
            return switch (type) {
                case "abstract_to_concrete" -> "추상 개념을 구체화하는 데 어려움";
                case "terminology_focus" -> "용어 이해에 집중하는 경향";
                case "process_confusion" -> "과정 이해에 혼란";
                case "application_gap" -> "응용 능력 부족";
                case "math_gap" -> "수학적 기초 부족";
                default -> "학습 장애 원인 분석 중";
            };
        }
    }

    @Builder
    @Schema(description = "주요 학습 장애 원인")
    public record PrimaryDifficultyResponse(
            @Schema(description = "장애 유형", example = "abstract_to_concrete")
            String type,

            @Schema(description = "라벨")
            String label
    ) {
    }

    @Builder
    @Schema(description = "막힌 개념 요약")
    public record StuckConceptSummaryResponse(
            @Schema(description = "개념")
            String concept,

            @Schema(description = "해결 여부")
            Boolean resolved
    ) {
    }

    @Builder
    @Schema(description = "학습 행동 인사이트")
    public record LearningInsightsResponse(
            @Schema(description = "질문 스타일 요약")
            QuestionStyleResponse questionStyle,

            @Schema(description = "개념 연결 능력 요약")
            ConceptLinkingResponse conceptLinking
    ) {
        public static LearningInsightsResponse from(Dashboard dashboard) {
            Dashboard.LearningBehavior learningBehavior = dashboard.getLearningBehavior();
            if (learningBehavior == null) {
                return LearningInsightsResponse.builder()
                        .questionStyle(null)
                        .conceptLinking(null)
                        .build();
            }

            QuestionStyleResponse questionStyle = null;
            if (learningBehavior.getQuestionTypeRatio() != null) {
                questionStyle = QuestionStyleResponse.from(learningBehavior.getQuestionTypeRatio());
            }

            ConceptLinkingResponse conceptLinking = null;
            if (learningBehavior.getConceptLinkScore() != null) {
                conceptLinking = ConceptLinkingResponse.from(learningBehavior.getConceptLinkScore());
            }

            return LearningInsightsResponse.builder()
                    .questionStyle(questionStyle)
                    .conceptLinking(conceptLinking)
                    .build();
        }
    }

    @Builder
    @Schema(description = "질문 스타일 요약")
    public record QuestionStyleResponse(
            @Schema(description = "주요 질문 유형", example = "mechanism")
            String dominantType,

            @Schema(description = "요약")
            String summary
    ) {
        public static QuestionStyleResponse from(Dashboard.QuestionTypeRatio questionTypeRatio) {
            Double definition = questionTypeRatio.getDefinition() != null ? questionTypeRatio.getDefinition() : 0.0;
            Double mechanism = questionTypeRatio.getMechanism() != null ? questionTypeRatio.getMechanism() : 0.0;
            Double comparison = questionTypeRatio.getComparison() != null ? questionTypeRatio.getComparison() : 0.0;

            String dominantType;
            String summary;

            if (mechanism >= definition && mechanism >= comparison) {
                dominantType = "mechanism";
                summary = "작동 원리를 이해하려는 질문이 많음";
            } else if (comparison >= definition && comparison >= mechanism) {
                dominantType = "comparison";
                summary = "개념 간 비교를 통한 이해를 시도함";
            } else {
                dominantType = "definition";
                summary = "정의와 기본 개념 이해에 집중함";
            }

            return QuestionStyleResponse.builder()
                    .dominantType(dominantType)
                    .summary(summary)
                    .build();
        }
    }

    @Builder
    @Schema(description = "개념 연결 능력 요약")
    public record ConceptLinkingResponse(
            @Schema(description = "수준", example = "high")
            String level,

            @Schema(description = "요약")
            String summary
    ) {
        public static ConceptLinkingResponse from(Double conceptLinkScore) {
            String level;
            String summary;

            if (conceptLinkScore >= 0.7) {
                level = "high";
                summary = "개념 간 관계를 스스로 연결하려는 시도가 관찰됨";
            } else if (conceptLinkScore >= 0.4) {
                level = "medium";
                summary = "개념 간 연결을 시도하지만 일관성이 부족함";
            } else {
                level = "low";
                summary = "개념 간 연결 시도가 적음";
            }

            return ConceptLinkingResponse.builder()
                    .level(level)
                    .summary(summary)
                    .build();
        }
    }

    @Builder
    @Schema(description = "다음 학습 지도 가이드")
    public record NextGuidanceResponse(
            @Schema(description = "다음 수업에서 반드시 다룰 개념")
            List<String> focusConcepts,

            @Schema(description = "강사용 지도 전략 요약")
            String teachingTip,

            @Schema(description = "바로 제시 가능한 과제/활동")
            String recommendedPractice
    ) {
        public static NextGuidanceResponse from(Dashboard dashboard) {
            Dashboard.InstructionalGuidance instructionalGuidance = dashboard.getInstructionalGuidance();
            if (instructionalGuidance == null) {
                return NextGuidanceResponse.builder()
                        .focusConcepts(List.of())
                        .teachingTip("")
                        .recommendedPractice("")
                        .build();
            }

            List<String> focusConcepts = instructionalGuidance.getNextFocusConcepts() != null
                    ? instructionalGuidance.getNextFocusConcepts()
                    : List.of();

            String teachingTip = "";
            if (instructionalGuidance.getTeachingRecommendations() != null
                    && !instructionalGuidance.getTeachingRecommendations().isEmpty()) {
                teachingTip = String.join(", ", instructionalGuidance.getTeachingRecommendations());
            }

            String recommendedPractice = instructionalGuidance.getRecommendedPractice() != null
                    ? instructionalGuidance.getRecommendedPractice()
                    : "";

            return NextGuidanceResponse.builder()
                    .focusConcepts(focusConcepts)
                    .teachingTip(teachingTip)
                    .recommendedPractice(recommendedPractice)
                    .build();
        }
    }
}
