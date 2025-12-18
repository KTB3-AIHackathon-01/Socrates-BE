package com.socrates.app.mvc.analytics.chat.controller;

import com.socrates.app.mvc.analytics.chat.dto.ChatMessageResponse;
import com.socrates.app.mvc.analytics.chat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/analytics/messages")
@RestController
@Tag(name = "ChatMessages", description = "채팅 메시지 관리 API")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "세션별 메시지 페이지 조회", description = "ChatSessionID로 채팅 메시지를 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<PagedModel<ChatMessageResponse>> getMessagesBySessionId(
            @PathVariable UUID sessionId,
            @RequestHeader("X-Student-Id") UUID studentId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        PagedModel<ChatMessageResponse> response = chatMessageService.getMessagesBySessionId(sessionId, studentId, page, size);
        return ResponseEntity.ok(response);
    }
}
