package com.socrates.app.webflux.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastApiChatResponse {

    private Boolean success;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    private ResponseData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        @JsonProperty("user_facing_message")
        private String userFacingMessage;

        @JsonProperty("is_stuck")
        private Boolean isStuck;

        @JsonProperty("next_action")
        private String nextAction;
    }
}
