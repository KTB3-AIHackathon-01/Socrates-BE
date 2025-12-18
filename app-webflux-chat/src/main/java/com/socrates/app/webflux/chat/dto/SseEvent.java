package com.socrates.app.webflux.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseEvent {

    private String event;
    private Object data;

    public static SseEvent chatMessage(String content) {
        return SseEvent.builder()
                .event("message")
                .data(content)
                .build();
    }

    public static SseEvent chatEnd(String sessionId) {
        return SseEvent.builder()
                .event("chat_end")
                .data(ChatEndData.builder().sessionId(sessionId).build())
                .build();
    }

    public static SseEvent report(String sessionId, String markdown) {
        return SseEvent.builder()
                .event("report")
                .data(ReportData.builder()
                        .sessionId(sessionId)
                        .markdown(markdown)
                        .build())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatEndData {
        private String sessionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportData {
        private String sessionId;
        private String markdown;
    }
}
