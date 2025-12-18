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
public class ChatHistoryResponse {

    private String id;

    private String userMessage;

    private String assistantMessage;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private String status;

    private Boolean isComplete;
}
