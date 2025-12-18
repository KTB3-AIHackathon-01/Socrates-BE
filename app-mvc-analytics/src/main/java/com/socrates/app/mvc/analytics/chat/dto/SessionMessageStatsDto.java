package com.socrates.app.mvc.analytics.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionMessageStatsDto {

    private String sessionId;

    private Long messageCount;

    private LocalDateTime firstMessage;

    private LocalDateTime lastMessage;
}
