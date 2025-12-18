package com.socrates.app.mvc.analytics.chat.dto;

import com.socrates.app.mvc.analytics.chat.domain.ChatSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "채팅 세션 정보")
public record ChatSessionResponse(
        @Schema(description = "채팅 세션 ID", format = "uuid")
        UUID sessionId,
        @Schema(description = "학생 ID", format = "uuid")
        UUID studentId,
        @Schema(description = "채팅 세션 제목")
        String name,
        @Schema(description = "세션 시작 시각", type = "string", format = "date-time")
        LocalDateTime startedAt,
        @Schema(description = "세션 종료 시각", type = "string", format = "date-time", nullable = true)
        LocalDateTime endedAt
) {

    public static ChatSessionResponse from(ChatSession chatSession) {
        return ChatSessionResponse.builder()
                .sessionId(chatSession.getId())
                .studentId(chatSession.getStudent().getId())
                .name(chatSession.getName())
                .startedAt(chatSession.getStartedAt())
                .endedAt(chatSession.getEndedAt())
                .build();
    }
}
