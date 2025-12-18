package com.socrates.app.mvc.analytics.dashboard.dto;

import com.socrates.app.mvc.analytics.chat.dto.ResponseTimeStatsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTimeResponse {

    private Double avgResponseTime;

    private String avgResponseTimeFormatted;

    private Long minResponseTime;

    private Long maxResponseTime;

    public static ResponseTimeResponse from(ResponseTimeStatsDto dto) {
        if (dto == null) {
            return ResponseTimeResponse.builder()
                    .avgResponseTime(0.0)
                    .avgResponseTimeFormatted("0초")
                    .minResponseTime(0L)
                    .maxResponseTime(0L)
                    .build();
        }

        String formatted = formatResponseTime(dto.getAvgResponseTime());

        return ResponseTimeResponse.builder()
                .avgResponseTime(dto.getAvgResponseTime())
                .avgResponseTimeFormatted(formatted)
                .minResponseTime(dto.getMinResponseTime())
                .maxResponseTime(dto.getMaxResponseTime())
                .build();
    }

    private static String formatResponseTime(Double millis) {
        if (millis == null) return "0초";
        double seconds = millis / 1000.0;
        return String.format("%.1f초", seconds);
    }
}
