package com.socrates.app.webflux.chat.controller;

import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.dto.ChatTitleResponse;
import com.socrates.app.webflux.chat.service.ChatService;
import com.socrates.app.webflux.chat.service.ChatTitleService;
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

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
