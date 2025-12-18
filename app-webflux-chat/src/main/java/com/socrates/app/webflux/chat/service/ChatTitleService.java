package com.socrates.app.webflux.chat.service;

import com.socrates.app.webflux.chat.client.ChatTitleGeneratorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatTitleService {

    private final ChatTitleGeneratorClient titleGeneratorClient;

    public Mono<String> generateChatRoomTitle(String firstMessage) {
        log.info("채팅방 제목 생성 요청 - 메시지: {}", firstMessage);

        return titleGeneratorClient.generateTitle(firstMessage)
                .doOnSuccess(title -> log.info("채팅방 제목 생성 완료: {}", title))
                .doOnError(error -> log.error("채팅방 제목 생성 실패: {}", error.getMessage()))
                .onErrorReturn("새 채팅");
    }
}
