package com.socrates.app.mvc.analytics.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "채팅 세션 생성 요청 본문")
public record ChatSessionRequest(
        @Schema(description = "세션 ID", format = "uuid")
        @NotNull
        UUID sessionId,
        @Schema(description = "세션을 생성할 학생 ID", format = "uuid")
        @NotNull
        UUID studentId,
        @Schema(description = "채팅 세션 제목")
        String name
) {
}
