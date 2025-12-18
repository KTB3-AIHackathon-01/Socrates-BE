package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatsResponse {

    private String sessionId;

    private Long messageCount;

    private LocalDateTime firstMessage;

    private LocalDateTime lastMessage;

    private String duration;
}
