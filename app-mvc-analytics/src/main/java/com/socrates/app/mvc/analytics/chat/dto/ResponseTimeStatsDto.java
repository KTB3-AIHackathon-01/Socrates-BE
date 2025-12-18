package com.socrates.app.mvc.analytics.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTimeStatsDto {

    private Double avgResponseTime;

    private Long minResponseTime;

    private Long maxResponseTime;
}
