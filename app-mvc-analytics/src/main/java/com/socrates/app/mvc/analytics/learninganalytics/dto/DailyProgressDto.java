package com.socrates.app.mvc.analytics.learninganalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyProgressDto {

    private String date;

    private Double avgProgress;

    private Long count;
}
