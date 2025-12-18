package com.socrates.app.mvc.analytics.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMessageCountDto {

    private String date;

    private Long messageCount;
}
