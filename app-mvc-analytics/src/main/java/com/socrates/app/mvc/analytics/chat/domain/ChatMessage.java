package com.socrates.app.mvc.analytics.chat.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private String userId;

    private String sessionId;

    private String userMessage;

    private String assistantMessage;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Builder.Default
    private MessageStatus status = MessageStatus.PENDING;

    @Builder.Default
    private Boolean isComplete = false;

    public enum MessageStatus {
        PENDING,
        STREAMING,
        COMPLETED,
        FAILED
    }
}
