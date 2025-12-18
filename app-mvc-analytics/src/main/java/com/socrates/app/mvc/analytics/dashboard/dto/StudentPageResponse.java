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
public class StudentPageResponse {

    private List<StudentStatisticsResponse> content;

    private Long totalElements;

    private Integer totalPages;

    private Integer currentPage;

    public static StudentPageResponse of(List<StudentStatisticsResponse> content, long total, int page, int size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return StudentPageResponse.builder()
                .content(content)
                .totalElements(total)
                .totalPages(totalPages)
                .currentPage(page)
                .build();
    }
}
