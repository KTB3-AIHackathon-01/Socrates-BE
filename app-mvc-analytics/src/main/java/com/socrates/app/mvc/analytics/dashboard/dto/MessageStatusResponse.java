package com.socrates.app.mvc.analytics.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusResponse {

    private Long completed;

    private Long failed;

    private Long inProgress;

    private Long pending;

    private Long total;

    private Double successRate;

    public static MessageStatusResponse of(long completed, long failed, long inProgress, long pending) {
        long total = completed + failed + inProgress + pending;
        double successRate = total > 0 ? (double) completed / total : 0.0;

        return MessageStatusResponse.builder()
                .completed(completed)
                .failed(failed)
                .inProgress(inProgress)
                .pending(pending)
                .total(total)
                .successRate(successRate)
                .build();
    }
}
