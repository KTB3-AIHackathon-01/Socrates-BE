package com.socrates.app.webflux.chat.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.socrates.app.webflux.chat.client.ChatTitleGeneratorClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiChatTitleGenerator implements ChatTitleGeneratorClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model:gpt-5-nano}")
    private String model;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    public Mono<String> generateTitle(String firstMessage) {
        log.info("채팅방 제목 생성 시작 - 첫 메시지: {}", firstMessage);

        OpenAiRequest request = OpenAiRequest.builder()
                .model(model)
                .messages(List.of(
                        Message.builder()
                                .role("system")
                                .content("당신은 채팅방 제목을 만드는 전문가입니다. 사용자의 첫 메시지를 요약하여 간결한 채팅방 제목을 생성하세요. 5-10단어의 한글 제목만 반환하고, 따옴표나 설명은 제외하세요.")
                                .build(),
                        Message.builder()
                                .role("user")
                                .content(firstMessage)
                                .build()
                ))
                .temperature(0.7)
                .maxTokens(200)
                .build();

        return webClientBuilder.build()
                .post()
                .uri(OPENAI_API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent().trim())
                .doOnSuccess(title -> log.info("채팅방 제목 생성 완료: {}", title))
                .doOnError(error -> log.error("채팅방 제목 생성 실패: {}", error.getMessage()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    private static class OpenAiRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        @JsonProperty("max_tokens")
        private Integer maxTokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    private static class Message {
        private String role;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OpenAiResponse {
        private List<Choice> choices;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Choice {
        private Message message;
    }
}
