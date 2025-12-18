package com.socrates.app.webflux.chat.controller;

import com.socrates.app.webflux.chat.dto.ChatHistoryResponse;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.dto.ChatTitleResponse;
import com.socrates.app.webflux.chat.dto.SessionReportResponse;
import com.socrates.app.webflux.chat.service.ChatMessageService;
import com.socrates.app.webflux.chat.service.ChatService;
import com.socrates.app.webflux.chat.service.ChatTitleService;
import com.socrates.app.webflux.chat.service.SessionReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatTitleService chatTitleService;
    private final ChatMessageService chatMessageService;
    private final SessionReportService sessionReportService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ChatRequest request) {
        log.info("채팅 스트림 요청 수신 - userId: {}, sessionId: {}",
                request.getUserId(), request.getSessionId());

        return chatService.streamChat(request);
    }

    @PostMapping("/title")
    public Mono<ChatTitleResponse> generateTitle(@RequestBody ChatRequest request) {
        log.info("채팅방 제목 생성 요청 - message: {}", request.getMessage());

        return chatTitleService.generateChatRoomTitle(request.getMessage())
                .map(title -> ChatTitleResponse.builder()
                        .title(title)
                        .build());
    }

    @GetMapping("/history/{sessionId}")
    public Flux<ChatHistoryResponse> getChatHistory(@PathVariable String sessionId) {
        log.info("채팅 기록 조회 요청 - sessionId: {}", sessionId);

        return chatMessageService.getChatHistory(sessionId)
                .map(message -> ChatHistoryResponse.builder()
                        .id(message.getId())
                        .userMessage(message.getUserMessage())
                        .assistantMessage(message.getAssistantMessage())
                        .createdAt(message.getCreatedAt())
                        .completedAt(message.getCompletedAt())
                        .status(message.getStatus() != null ? message.getStatus().name() : null)
                        .isComplete(message.getIsComplete())
                        .build());
    }

    @GetMapping("/report/{sessionId}")
    public Mono<SessionReportResponse> getReport(@PathVariable String sessionId) {
        log.info("리포트 조회/생성 요청 - sessionId: {}", sessionId);

        return sessionReportService.generateReport(sessionId)
                .map(report -> SessionReportResponse.builder()
                        .id(report.getId())
                        .userId(report.getUserId())
                        .sessionId(report.getSessionId())
                        .reportData(report.getReportData())
                        .reportJson(report.getReportJson())
                        .createdAt(report.getCreatedAt())
                        .status(report.getStatus() != null ? report.getStatus().name() : null)
                        .build());
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
