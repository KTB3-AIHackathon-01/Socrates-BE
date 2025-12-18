package com.socrates.app.mvc.analytics.session.controller;

import com.socrates.app.mvc.analytics.session.dto.ChatSessionRequest;
import com.socrates.app.mvc.analytics.session.dto.ChatSessionResponse;
import com.socrates.app.mvc.analytics.session.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/sessions")
@RestController
@Tag(name = "Sessions", description = "채팅 세션 관리 API")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @Operation(summary = "채팅 세션 생성", description = "학생의 새로운 채팅 세션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "채팅 세션 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ChatSessionResponse> createChatSession(@Valid @RequestBody ChatSessionRequest chatSessionRequest) {
        ChatSessionResponse response = chatSessionService.createChatSession(chatSessionRequest);
        return ResponseEntity.created(URI.create("/api/sessions/" + response.sessionId()))
                .body(response);
    }

    @Operation(summary = "채팅 세션 조회", description = "세션 ID로 채팅 세션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 세션 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))),
            @ApiResponse(responseCode = "404", description = "채팅 세션을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionResponse> getChatSession(@PathVariable UUID sessionId) {
        ChatSessionResponse response = chatSessionService.getChatSession(sessionId);
        return ResponseEntity.ok(response);
    }
}
