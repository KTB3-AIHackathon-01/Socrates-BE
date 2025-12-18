package com.socrates.app.mvc.analytics.chat.dto;

import com.socrates.app.mvc.analytics.chat.domain.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "채팅 메시지 응답")
public record ChatMessageResponse(
        @Schema(description = "메시지 ID")
        String messageId,
        @Schema(description = "사용자 메시지")
        String userMessage,
        @Schema(description = "어시스턴트 메시지")
        String assistantMessage,
        @Schema(description = "메시지 생성 시각", type = "string", format = "date-time")
        LocalDateTime createdAt,
        @Schema(description = "메시지 완료 시각", type = "string", format = "date-time")
        LocalDateTime completedAt,
        @Schema(description = "메시지 상태")
        ChatMessage.MessageStatus status,
        @Schema(description = "세션 완료 여부")
        Boolean isComplete
) {

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .userMessage(chatMessage.getUserMessage())
                .assistantMessage(chatMessage.getAssistantMessage())
                .createdAt(chatMessage.getCreatedAt())
                .completedAt(chatMessage.getCompletedAt())
                .status(chatMessage.getStatus())
                .isComplete(chatMessage.getIsComplete())
                .build();
    }
}
