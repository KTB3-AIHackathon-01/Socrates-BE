package com.socrates.app.webflux.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "session_reports")
public class SessionReport {

    @Id
    private String id;

    private String userId;

    private String sessionId;

    private String reportData;

    private String reportJson;

    private LocalDateTime createdAt;

    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    public enum ReportStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
