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
public class FastApiReportResponse {

    @JsonProperty("markdown")
    private String markdown;

    @JsonProperty("json")
    private String json;
}
