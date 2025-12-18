package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUsersResponse {

    private Long activeUsers;

    private String timeWindow;

    private LocalDateTime timestamp;

    public static ActiveUsersResponse of(long activeUsers, int minutes) {
        return ActiveUsersResponse.builder()
                .activeUsers(activeUsers)
                .timeWindow("최근 " + minutes + "분")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
