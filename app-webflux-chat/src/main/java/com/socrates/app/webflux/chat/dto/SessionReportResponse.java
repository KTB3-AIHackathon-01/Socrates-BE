package com.socrates.app.webflux.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionReportResponse {

    private String id;

    private String userId;

    private String sessionId;

    private String reportData;

    private String reportJson;

    private LocalDateTime createdAt;

    private String status;
}
