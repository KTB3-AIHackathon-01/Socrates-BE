package com.socrates.app.webflux.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastApiChatRequest {

    private DataWrapper data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        private List<String> user_input;
    }
}
