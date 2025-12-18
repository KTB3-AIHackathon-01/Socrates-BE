package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.client.FastApiClient;
import com.socrates.app.webflux.chat.domain.ChatMessage;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final FastApiClient fastApiClient;
    private final ChatMessageService chatMessageService;
    private final SessionReportService sessionReportService;

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
        log.info("사용자 채팅 스트림 처리 시작: {}", request.getUserId());

        return chatMessageService.savePendingMessage(request)
                .flatMapMany(savedMessage -> processResponse(request, savedMessage))
                .map(this::toServerSentEvent)
                .doOnNext(sse -> log.debug("응답 스트림 전송: {}", sse.data()))
                .doOnComplete(() -> log.info("사용자 채팅 스트림 처리 완료: {}", request.getUserId()));
    }

    private Flux<String> processResponse(ChatRequest request, ChatMessage savedMessage) {
        return fastApiClient.chat(request)
                .doOnNext(response -> handleSessionCompletion(request, response))
                .flatMapMany(response -> saveAndStreamResponse(savedMessage, response))
                .onErrorResume(error -> handleError(savedMessage, error));
    }

    private void handleSessionCompletion(ChatRequest request, FastApiChatResponse response) {
        if (Boolean.TRUE.equals(response.getIsComplete())) {
            log.info("세션 완료 감지 - sessionId: {}, 백그라운드 리포트 생성 시작", request.getSessionId());
            triggerReportGeneration(request.getUserId(), request.getSessionId());
        }
    }

    private void triggerReportGeneration(String userId, String sessionId) {
        sessionReportService.generateReportAsync(userId, sessionId)
                .subscribe(
                        null,
                        error -> log.error("백그라운드 리포트 생성 실패: {}", error.getMessage()),
                        () -> log.info("백그라운드 리포트 생성 작업 완료")
                );
    }

    private Flux<String> saveAndStreamResponse(ChatMessage savedMessage, FastApiChatResponse response) {
        return chatMessageService.updateCompletedMessage(
                        savedMessage.getId(),
                        response.getContent(),
                        Boolean.TRUE.equals(response.getIsComplete())
                )
                .thenMany(splitContentToWords(response.getContent()));
    }

    private Flux<String> splitContentToWords(String content) {
        String[] words = content.split("(?<=\\s)|(?=\\s)");
        return Flux.fromArray(words);
    }

    private Flux<String> handleError(ChatMessage savedMessage, Throwable error) {
        log.error("채팅 스트림 오류 발생: {}", error.getMessage());
        chatMessageService.updateFailedMessage(savedMessage.getId()).subscribe();
        return Flux.error(error);
    }

    private ServerSentEvent<String> toServerSentEvent(String chunk) {
        return ServerSentEvent.<String>builder()
                .data(chunk)
                .build();
    }
}
