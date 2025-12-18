package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.client.FastApiClient;
import com.socrates.app.webflux.chat.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final FastApiClient fastApiClient;
    private final ChatMessageService chatMessageService;

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
        log.info("사용자 채팅 스트림 처리 시작: {}", request.getUserId());

        return chatMessageService.savePendingMessage(request)
                .flatMapMany(savedMessage ->
                        fastApiClient.streamChat(request)
                                .collectList()
                                .flatMapMany(chunks -> {
                                    String fullResponse = String.join("", chunks);
                                    return chatMessageService.updateCompletedMessage(savedMessage.getId(), fullResponse)
                                            .thenMany(Flux.fromIterable(chunks));
                                })
                                .map(data -> ServerSentEvent.<String>builder()
                                        .data(data)
                                        .build())
                                .doOnNext(sse -> log.debug("응답 스트림 전송: {}", sse.data()))
                                .doOnError(error -> {
                                    log.error("채팅 스트림 오류 발생: {}", error.getMessage());
                                    chatMessageService.updateFailedMessage(savedMessage.getId()).subscribe();
                                })
                                .doOnComplete(() -> log.info("사용자 채팅 스트림 처리 완료: {}", request.getUserId()))
                );
    }
}
