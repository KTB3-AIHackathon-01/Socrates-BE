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
public class UserMessageStatsDto {

    private String userId;

    private Long messageCount;

    private LocalDateTime lastMessageTime;
}
