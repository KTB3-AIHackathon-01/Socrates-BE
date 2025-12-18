package com.socrates.app.mvc.analytics.chat.repository;

import com.socrates.app.mvc.analytics.chat.domain.ChatMessage;
import com.socrates.app.mvc.analytics.chat.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepositoryCustom {

    // 메시지 수 통계
    long countMessagesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    long countTodayMessages(LocalDateTime startOfDay, LocalDateTime endOfDay);

    long countMessagesByUserIdsAndDateRange(List<String> userIds, LocalDateTime startDate, LocalDateTime endDate);

    // 일별/시간대별 통계
    List<DailyMessageCountDto> getDailyMessageCount(LocalDateTime startDate, LocalDateTime endDate);

    List<HourlyMessageCountDto> getHourlyMessageCount(LocalDateTime startDate, LocalDateTime endDate);

    // 학생별 통계
    List<UserMessageStatsDto> getUserMessageStats(List<String> userIds);

    List<UserMessageStatsDto> getUserMessageStatsByDateRange(List<String> userIds, LocalDateTime startDate, LocalDateTime endDate);

    // 최근 활동
    List<ChatMessage> findLatestMessagesByUserIds(List<String> userIds);

    Long countDistinctActiveUsers(LocalDateTime sinceDateTime);

    // 응답 시간 분석
    ResponseTimeStatsDto getAverageResponseTime();

    // 세션별 통계
    List<SessionMessageStatsDto> getSessionMessageStats();

    // 메시지 상태별 통계
    long countByStatus(ChatMessage.MessageStatus status);
}
