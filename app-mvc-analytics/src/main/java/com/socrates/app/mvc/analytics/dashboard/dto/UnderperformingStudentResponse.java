package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderperformingStudentResponse {

    private List<UnderperformingStudent> students;

    private Long totalCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnderperformingStudent {
        private String studentId;
        private String studentName;
        private Double progressScore;
        private List<String> unresolvedConcepts;
        private String stuckDuration;
    }
}
