package com.socrates.app.mvc.analytics.chat.controller;

import com.socrates.app.mvc.analytics.chat.dto.ChatSessionRequest;
import com.socrates.app.mvc.analytics.chat.dto.ChatSessionResponse;
import com.socrates.app.mvc.analytics.chat.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/analytics/sessions")
@RestController
@Tag(name = "ChatSessions", description = "채팅방 세션 관리 API")
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
        return ResponseEntity.created(URI.create("/api/analytics/sessions/" + response.sessionId()))
                .body(response);
    }

    @Operation(summary = "채팅 세션 조회", description = "세션 ID로 채팅 세션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 세션 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))),
            @ApiResponse(responseCode = "404", description = "채팅 세션을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionResponse> getChatSession(@PathVariable String sessionId) {
        ChatSessionResponse response = chatSessionService.getChatSession(sessionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "학생별 채팅 목록 조회", description = "학생별 세션 목록을 페이지로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "세션 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatSessionResponse.class)))
    })
    @GetMapping("/student")
    public ResponseEntity<PagedModel<ChatSessionResponse>> getChatSessionList(
            @RequestHeader("X-Student-Id") UUID studentId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        PagedModel<ChatSessionResponse> response = chatSessionService.getChatSessions(studentId, page, size);
        return ResponseEntity.ok(response);
    }
}
