package com.socrates.app.webflux.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "메시지는 비어 있을 수 없습니다.")
    private String message;

    private String userId;

    private String sessionId;
}
