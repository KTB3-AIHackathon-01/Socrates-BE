package com.socrates.app.webflux.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socrates.app.webflux.chat.client.FastApiChatClient;
import com.socrates.app.webflux.chat.domain.ChatMessage;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatRequest;
import com.socrates.app.webflux.chat.dto.FastApiChatResponse;
import com.socrates.app.webflux.chat.dto.SseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final FastApiChatClient fastApiChatClient;
    private final ChatMessageService chatMessageService;
    private final SessionReportService sessionReportService;
    private final ObjectMapper objectMapper;

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
        log.info("사용자 채팅 스트림 처리 시작: {}", request.getUserId());

        Sinks.Many<String> reportSink = Sinks.many().multicast().onBackpressureBuffer();
        sessionReportService.registerReportSink(request.getSessionId(), reportSink);

        Flux<ServerSentEvent<String>> reportEventFlux = reportSink.asFlux()
                .map(markdown -> createReportEvent(request.getSessionId(), markdown))
                .doOnNext(event -> log.info("리포트 SSE 이벤트 생성 - sessionId: {}", request.getSessionId()));

        Flux<ServerSentEvent<String>> chatFlux = chatMessageService.savePendingMessage(request)
                .flatMapMany(savedMessage -> processResponse(request, savedMessage, reportSink))
                .doOnError(error -> log.error("채팅 스트림 오류: {}", error.getMessage()))
                .doOnComplete(() -> log.info("채팅 메시지 스트림 완료: {}", request.getUserId()));

        return chatFlux.mergeWith(reportEventFlux)
                .doOnComplete(() -> log.info("SSE 스트림 전체 완료: {}", request.getUserId()));
    }

    private Flux<ServerSentEvent<String>> processResponse(
            ChatRequest request,
            ChatMessage savedMessage,
            Sinks.Many<String> reportSink) {

        return loadChatHistory(request)
                .map(this::toFastApiRequest)
                .flatMapMany(fastApiRequest ->
                        fastApiChatClient.chat(fastApiRequest)
                                .flatMapMany(response -> {
                                    if (Boolean.TRUE.equals(response.getIsCompleted())) {
                                        return handleCompletedSession(request, savedMessage, response, reportSink);
                                    } else {
                                        return saveAndStreamResponse(savedMessage, response);
                                    }
                                })
                                .onErrorResume(error -> handleError(savedMessage, error))
                );
    }

    private Mono<ChatRequest> loadChatHistory(ChatRequest request) {
        return chatMessageService.findByUserIdAndSessionId(request.getUserId(), request.getSessionId())
                .filter(msg -> msg.getStatus() == ChatMessage.MessageStatus.COMPLETED)
                .map(msg -> ChatRequest.ChatHistoryItem.builder()
                        .userMessage(msg.getUserMessage())
                        .assistantMessage(msg.getAssistantMessage())
                        .build())
                .collectList()
                .map(history -> {
                    request.setHistory(history);
                    log.info("채팅 히스토리 로드 완료 - sessionId: {}, count: {}", request.getSessionId(), history.size());
                    return request;
                });
    }

    private FastApiChatRequest toFastApiRequest(ChatRequest request) {
        List<String> userInputList = new ArrayList<>();

        if (request.getHistory() != null) {
            for (ChatRequest.ChatHistoryItem item : request.getHistory()) {
                userInputList.add(item.getUserMessage());
                userInputList.add(item.getAssistantMessage());
            }
        }

        userInputList.add(request.getMessage());

        FastApiChatRequest.DataWrapper dataWrapper = FastApiChatRequest.DataWrapper.builder()
                .user_input(userInputList)
                .build();

        return FastApiChatRequest.builder()
                .data(dataWrapper)
                .build();
    }

    private Flux<ServerSentEvent<String>> handleCompletedSession(
            ChatRequest request,
            ChatMessage savedMessage,
            FastApiChatResponse response,
            Sinks.Many<String> reportSink) {

        log.info("세션 완료 감지 - sessionId: {}", request.getSessionId());

        return saveAndStreamResponse(savedMessage, response)
                .concatWith(Flux.defer(() -> {
                    ServerSentEvent<String> chatEndEvent = createChatEndEvent(request.getSessionId());
                    triggerReportGeneration(request.getUserId(), request.getSessionId());
                    return Flux.just(chatEndEvent);
                }));
    }

    private void triggerReportGeneration(String userId, String sessionId) {
        log.info("백그라운드 리포트 생성 시작 - sessionId: {}", sessionId);

        sessionReportService.generateReportAsync(userId, sessionId)
                .subscribe(
                        null,
                        error -> log.error("백그라운드 리포트 생성 실패: {}", error.getMessage()),
                        () -> log.info("백그라운드 리포트 생성 작업 완료 - sessionId: {}", sessionId)
                );
    }

    private Flux<ServerSentEvent<String>> saveAndStreamResponse(
            ChatMessage savedMessage,
            FastApiChatResponse response) {

        String content = response.getData() != null && response.getData().getUserFacingMessage() != null
                ? response.getData().getUserFacingMessage()
                : "";

        return chatMessageService.updateCompletedMessage(
                        savedMessage.getId(),
                        content,
                        Boolean.TRUE.equals(response.getIsCompleted())
                )
                .thenMany(splitContentToWords(content))
                .map(this::toMessageEvent);
    }

    private Flux<String> splitContentToWords(String content) {
        String[] words = content.split("(?<=\\s)|(?=\\s)");
        return Flux.fromArray(words);
    }

    private Flux<ServerSentEvent<String>> handleError(ChatMessage savedMessage, Throwable error) {
        log.error("채팅 스트림 오류 발생: {}", error.getMessage());
        chatMessageService.updateFailedMessage(savedMessage.getId()).subscribe();
        return Flux.error(error);
    }

    private ServerSentEvent<String> toMessageEvent(String chunk) {
        SseEvent event = SseEvent.chatMessage(chunk);
        return toServerSentEvent(event);
    }

    private ServerSentEvent<String> createChatEndEvent(String sessionId) {
        SseEvent event = SseEvent.chatEnd(sessionId);
        return toServerSentEvent(event);
    }

    private ServerSentEvent<String> createReportEvent(String sessionId, String markdown) {
        SseEvent event = SseEvent.report(sessionId, markdown);
        return toServerSentEvent(event);
    }

    private ServerSentEvent<String> toServerSentEvent(SseEvent event) {
        try {
            String data;
            if (event.getData() instanceof String) {
                data = (String) event.getData();
            } else {
                data = objectMapper.writeValueAsString(event.getData());
            }
            return ServerSentEvent.<String>builder()
                    .event(event.getEvent())
                    .data(data)
                    .build();
        } catch (JsonProcessingException e) {
            log.error("SSE 이벤트 JSON 변환 실패: {}", e.getMessage());
            return ServerSentEvent.<String>builder()
                    .event("error")
                    .data("{\"error\": \"Failed to serialize event\"}")
                    .build();
        }
    }
}
