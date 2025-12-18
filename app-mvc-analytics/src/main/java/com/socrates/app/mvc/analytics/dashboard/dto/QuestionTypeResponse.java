package com.socrates.app.mvc.analytics.dashboard.dto;

import com.socrates.app.mvc.analytics.learninganalytics.dto.QuestionTypeRatioDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTypeResponse {

    private QuestionTypeItem conceptUnderstanding;

    private QuestionTypeItem problemSolving;

    private QuestionTypeItem practicalLearning;

    private QuestionTypeItem review;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionTypeItem {
        private String label;
        private Double ratio;
        private Integer percent;
        private String color;

        public static QuestionTypeItem of(String label, Double ratio, String color) {
            return QuestionTypeItem.builder()
                    .label(label)
                    .ratio(ratio != null ? ratio : 0.0)
                    .percent(ratio != null ? (int) (ratio * 100) : 0)
                    .color(color)
                    .build();
        }
    }

    public static QuestionTypeResponse from(QuestionTypeRatioDto dto) {
        if (dto == null) {
            return QuestionTypeResponse.builder()
                    .conceptUnderstanding(QuestionTypeItem.of("개념 이해", 0.0, "#3b82f6"))
                    .problemSolving(QuestionTypeItem.of("문제 해결", 0.0, "#a855f7"))
                    .practicalLearning(QuestionTypeItem.of("심화 학습", 0.0, "#ec4899"))
                    .review(QuestionTypeItem.of("복습", 0.0, "#f59e0b"))
                    .build();
        }

        return QuestionTypeResponse.builder()
                .conceptUnderstanding(QuestionTypeItem.of("개념 이해", dto.getAvgDefinition(), "#3b82f6"))
                .problemSolving(QuestionTypeItem.of("문제 해결", dto.getAvgMechanism(), "#a855f7"))
                .practicalLearning(QuestionTypeItem.of("심화 학습", dto.getAvgComparison(), "#ec4899"))
                .review(QuestionTypeItem.of("복습", dto.getAvgReview(), "#f59e0b"))
                .build();
    }
}
