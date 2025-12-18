package com.socrates.app.mvc.analytics.dashboard.service;

import com.socrates.app.mvc.analytics.chat.dto.DailyMessageCountDto;
import com.socrates.app.mvc.analytics.chat.dto.HourlyMessageCountDto;
import com.socrates.app.mvc.analytics.chat.dto.UserMessageStatsDto;
import com.socrates.app.mvc.analytics.chat.repository.ChatMessageRepository;
import com.socrates.app.mvc.analytics.dashboard.dto.*;
import com.socrates.app.mvc.analytics.learninganalytics.domain.LearningAnalytics;
import com.socrates.app.mvc.analytics.learninganalytics.domain.StuckConcept;
import com.socrates.app.mvc.analytics.learninganalytics.dto.DailyProgressDto;
import com.socrates.app.mvc.analytics.learninganalytics.dto.QuestionTypeRatioDto;
import com.socrates.app.mvc.analytics.learninganalytics.repository.LearningAnalyticsRepository;
import com.socrates.app.mvc.analytics.student.domain.Student;
import com.socrates.app.mvc.analytics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final LearningAnalyticsRepository learningAnalyticsRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final StudentRepository studentRepository;

    // ==================== 전체 대시보드 ====================

    public DashboardResponse getDashboard(String instructorId, List<String> studentIds) {
        // 학생 ID 목록 조회 (instructorId가 있으면 해당 강사의 학생들만)
        List<String> targetStudentIds = resolveStudentIds(instructorId, studentIds);

        return DashboardResponse.builder()
                .metrics(getMetricsSection(targetStudentIds))
                .weeklyTrend(getWeeklyTrend(7))
                .questionTypes(getQuestionTypes(null))
                .studentStatistics(getStudentStatisticsList(targetStudentIds))
                .build();
    }

    private DashboardResponse.MetricsSection getMetricsSection(List<String> studentIds) {
        return DashboardResponse.MetricsSection.builder()
                .activeStudents(getActiveStudentsMetric(Period.WEEK))
                .todayQuestions(getTodayQuestionsMetric())
                .averageUnderstanding(getAverageUnderstandingMetric(Period.WEEK))
                .learningParticipation(getParticipationRateMetric())
                .build();
    }

    // ==================== 메트릭 카드 API ====================

    public MetricResponse getActiveStudentsMetric(Period period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentStart = getStartDate(now, period);
        LocalDateTime previousStart = getStartDate(currentStart, period);

        long currentCount = learningAnalyticsRepository.countActiveStudents(currentStart, now);
        long previousCount = learningAnalyticsRepository.countActiveStudents(previousStart, currentStart);

        return MetricResponse.of(currentCount, previousCount);
    }

    public MetricResponse getTodayQuestionsMetric() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.atTime(LocalTime.MAX);

        long todayCount = chatMessageRepository.countTodayMessages(todayStart, todayEnd);
        long yesterdayCount = chatMessageRepository.countTodayMessages(yesterdayStart, yesterdayEnd);

        return MetricResponse.of(todayCount, yesterdayCount);
    }

    public MetricResponse getAverageUnderstandingMetric(Period period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentStart = getStartDate(now, period);
        LocalDateTime previousStart = getStartDate(currentStart, period);

        Double currentScore = learningAnalyticsRepository.getAverageUnderstandingScoreByDateRange(currentStart, now);
        Double previousScore = learningAnalyticsRepository.getAverageUnderstandingScoreByDateRange(previousStart, currentStart);

        return MetricResponse.of(currentScore, previousScore);
    }

    public ParticipationRateResponse getParticipationRateMetric() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime previousWeekStart = weekStart.minusDays(7);

        long currentActive = learningAnalyticsRepository.countActiveStudents(weekStart, now);
        long previousActive = learningAnalyticsRepository.countActiveStudents(previousWeekStart, weekStart);
        long totalStudents = learningAnalyticsRepository.countDistinctStudents();

        return ParticipationRateResponse.of(currentActive, previousActive, totalStudents);
    }

    // ==================== 차트 데이터 API ====================

    public List<WeeklyTrendResponse> getWeeklyTrend(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<DailyMessageCountDto> messageCounts = chatMessageRepository.getDailyMessageCount(startDate, endDate);
        List<DailyProgressDto> progressStats = learningAnalyticsRepository.getDailyProgressStats(startDate, endDate);

        // 날짜별 매핑
        Map<String, Long> messageMap = messageCounts.stream()
                .collect(Collectors.toMap(DailyMessageCountDto::getDate, DailyMessageCountDto::getMessageCount));
        Map<String, Double> progressMap = progressStats.stream()
                .collect(Collectors.toMap(DailyProgressDto::getDate, DailyProgressDto::getAvgProgress));

        List<WeeklyTrendResponse> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            result.add(WeeklyTrendResponse.of(
                    dateStr,
                    dayOfWeek,
                    messageMap.getOrDefault(dateStr, 0L),
                    progressMap.get(dateStr)
            ));
        }

        return result;
    }

    public QuestionTypeResponse getQuestionTypes(Period period) {
        QuestionTypeRatioDto ratio;
        if (period == null || period == Period.ALL) {
            ratio = learningAnalyticsRepository.getAverageQuestionTypeRatio();
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = getStartDate(now, period);
            ratio = learningAnalyticsRepository.getAverageQuestionTypeRatioByDateRange(startDate, now);
        }

        return QuestionTypeResponse.from(ratio);
    }

    // ==================== 학생 현황 API ====================

    public StudentPageResponse getStudentStatistics(String instructorId, List<String> studentIds,
                                                     int page, int size, String sortBy, String order) {
        List<String> targetStudentIds = resolveStudentIds(instructorId, studentIds);
        List<StudentStatisticsResponse> allStats = getStudentStatisticsList(targetStudentIds);

        // 정렬
        Comparator<StudentStatisticsResponse> comparator = getComparator(sortBy);
        if ("DESC".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        allStats.sort(comparator);

        // 페이징
        int start = page * size;
        int end = Math.min(start + size, allStats.size());
        List<StudentStatisticsResponse> pagedContent = start < allStats.size()
                ? allStats.subList(start, end)
                : Collections.emptyList();

        return StudentPageResponse.of(pagedContent, allStats.size(), page, size);
    }

    public StudentDetailResponse getStudentDetail(String studentId) {
        LearningAnalytics latestAnalytics = learningAnalyticsRepository
                .findTopByStudentIdOrderByUpdatedAtDesc(studentId)
                .orElse(null);

        if (latestAnalytics == null) {
            return null;
        }

        // 학생 이름 조회
        String studentName = getStudentName(studentId);

        // 질문 수 조회
        long questionCount = chatMessageRepository.countByUserId(studentId);

        // 추세 계산
        String trend = calculateStudentTrend(studentId);

        return StudentDetailResponse.from(latestAnalytics, studentName, questionCount, trend);
    }

    private List<StudentStatisticsResponse> getStudentStatisticsList(List<String> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 최신 분석 데이터 조회
        List<LearningAnalytics> analyticsList = learningAnalyticsRepository.findLatestAnalyticsByStudentIds(studentIds);
        Map<String, LearningAnalytics> analyticsMap = analyticsList.stream()
                .collect(Collectors.toMap(LearningAnalytics::getStudentId, a -> a));

        // 메시지 통계 조회
        List<UserMessageStatsDto> messageStats = chatMessageRepository.getUserMessageStats(studentIds);
        Map<String, UserMessageStatsDto> messageMap = messageStats.stream()
                .collect(Collectors.toMap(UserMessageStatsDto::getUserId, m -> m));

        // 학생 이름 조회
        Map<String, String> nameMap = getStudentNames(studentIds);

        return studentIds.stream()
                .map(studentId -> buildStudentStatistics(studentId, analyticsMap, messageMap, nameMap))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private StudentStatisticsResponse buildStudentStatistics(String studentId,
                                                              Map<String, LearningAnalytics> analyticsMap,
                                                              Map<String, UserMessageStatsDto> messageMap,
                                                              Map<String, String> nameMap) {
        LearningAnalytics analytics = analyticsMap.get(studentId);
        UserMessageStatsDto messageStats = messageMap.get(studentId);

        Double understandingScore = null;
        LocalDateTime lastActivity = null;

        if (analytics != null && analytics.getLearningSummary() != null) {
            understandingScore = analytics.getLearningSummary().getMasteryRatio();
            lastActivity = analytics.getUpdatedAt();
        }

        if (messageStats != null && messageStats.getLastMessageTime() != null) {
            if (lastActivity == null || messageStats.getLastMessageTime().isAfter(lastActivity)) {
                lastActivity = messageStats.getLastMessageTime();
            }
        }

        String trend = calculateStudentTrend(studentId);

        return StudentStatisticsResponse.builder()
                .studentId(studentId)
                .studentName(nameMap.getOrDefault(studentId, "Unknown"))
                .questionCount(messageStats != null ? messageStats.getMessageCount() : 0L)
                .understandingScore(understandingScore)
                .understandingPercent(understandingScore != null ? (int) (understandingScore * 100) : 0)
                .understandingLevel(StudentStatisticsResponse.calculateUnderstandingLevel(understandingScore))
                .lastActivity(lastActivity)
                .lastActivityFormatted(formatLastActivity(lastActivity))
                .trend(trend)
                .trendIcon(StudentStatisticsResponse.getTrendIcon(trend))
                .build();
    }

    // ==================== 분석 API ====================

    public List<HourlyActivityResponse> getHourlyActivity(LocalDateTime startDate, LocalDateTime endDate) {
        List<HourlyMessageCountDto> hourlyStats = chatMessageRepository.getHourlyMessageCount(startDate, endDate);

        return hourlyStats.stream()
                .map(dto -> HourlyActivityResponse.builder()
                        .hour(dto.getHour())
                        .messageCount(dto.getMessageCount())
                        .build())
                .collect(Collectors.toList());
    }

    public UnderperformingStudentResponse getUnderperformingStudents(double threshold) {
        List<LearningAnalytics> stuckStudents = learningAnalyticsRepository.findStudentsWithUnresolvedStuckConcepts();
        long belowThresholdCount = learningAnalyticsRepository.countStudentsBelowProgressThreshold(threshold);

        Map<String, String> nameMap = getStudentNames(
                stuckStudents.stream().map(LearningAnalytics::getStudentId).collect(Collectors.toList())
        );

        List<UnderperformingStudentResponse.UnderperformingStudent> students = stuckStudents.stream()
                .map(analytics -> {
                    List<String> unresolvedConcepts = analytics.getLearningDifficulty() != null
                            && analytics.getLearningDifficulty().getStuckConcepts() != null
                            ? analytics.getLearningDifficulty().getStuckConcepts().stream()
                                    .filter(sc -> !Boolean.TRUE.equals(sc.getResolved()))
                                    .map(StuckConcept::getConcept)
                                    .collect(Collectors.toList())
                            : Collections.emptyList();

                    return UnderperformingStudentResponse.UnderperformingStudent.builder()
                            .studentId(analytics.getStudentId())
                            .studentName(nameMap.getOrDefault(analytics.getStudentId(), "Unknown"))
                            .progressScore(analytics.getLearningSummary() != null
                                    ? analytics.getLearningSummary().getOverallProgressScore() : null)
                            .unresolvedConcepts(unresolvedConcepts)
                            .stuckDuration(calculateStuckDuration(analytics))
                            .build();
                })
                .collect(Collectors.toList());

        return UnderperformingStudentResponse.builder()
                .students(students)
                .totalCount(belowThresholdCount)
                .build();
    }

    public ConceptMasteryResponse getConceptMastery(String concept) {
        long masteredCount = learningAnalyticsRepository.countStudentsWhoMasteredConcept(concept);
        long totalStudents = learningAnalyticsRepository.countDistinctStudents();

        return ConceptMasteryResponse.of(concept, masteredCount, totalStudents);
    }

    public ResponseTimeResponse getResponseTime() {
        return ResponseTimeResponse.from(chatMessageRepository.getAverageResponseTime());
    }

    public ActiveUsersResponse getActiveUsers(int minutes) {
        LocalDateTime sinceTime = LocalDateTime.now().minusMinutes(minutes);
        Long activeCount = chatMessageRepository.countDistinctActiveUsers(sinceTime);

        return ActiveUsersResponse.of(activeCount != null ? activeCount : 0L, minutes);
    }

    // ==================== 헬퍼 메서드 ====================

    private List<String> resolveStudentIds(String instructorId, List<String> studentIds) {
        if (studentIds != null && !studentIds.isEmpty()) {
            return studentIds;
        }

        if (instructorId != null) {
            // 강사의 학생 목록 조회 (TODO: InstructorRepository 연동 필요)
            return studentRepository.findAll().stream()
                    .filter(s -> s.getInstructor() != null
                            && s.getInstructor().getId().toString().equals(instructorId))
                    .map(s -> s.getId().toString())
                    .collect(Collectors.toList());
        }

        // 전체 학생
        return studentRepository.findAll().stream()
                .map(s -> s.getId().toString())
                .collect(Collectors.toList());
    }

    private String getStudentName(String studentId) {
        try {
            UUID uuid = UUID.fromString(studentId);
            return studentRepository.findById(uuid)
                    .map(Student::getName)
                    .orElse("Unknown");
        } catch (IllegalArgumentException e) {
            return "Unknown";
        }
    }

    private Map<String, String> getStudentNames(List<String> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UUID> uuids = studentIds.stream()
                .map(id -> {
                    try {
                        return UUID.fromString(id);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return studentRepository.findAllById(uuids).stream()
                .collect(Collectors.toMap(
                        s -> s.getId().toString(),
                        Student::getName
                ));
    }

    private String calculateStudentTrend(String studentId) {
        List<LearningAnalytics> recent = learningAnalyticsRepository
                .findTop2ByStudentIdOrderByUpdatedAtDesc(studentId);

        if (recent.size() < 2) {
            return "stable";
        }

        Double current = recent.get(0).getLearningSummary() != null
                ? recent.get(0).getLearningSummary().getMasteryRatio() : null;
        Double previous = recent.get(1).getLearningSummary() != null
                ? recent.get(1).getLearningSummary().getMasteryRatio() : null;

        return StudentStatisticsResponse.calculateTrend(current, previous);
    }

    private LocalDateTime getStartDate(LocalDateTime from, Period period) {
        return switch (period) {
            case WEEK -> from.minusDays(7);
            case MONTH -> from.minusMonths(1);
            case ALL -> LocalDateTime.of(2020, 1, 1, 0, 0);
        };
    }

    private String formatLastActivity(LocalDateTime lastActivity) {
        if (lastActivity == null) {
            return "활동 없음";
        }

        long minutes = ChronoUnit.MINUTES.between(lastActivity, LocalDateTime.now());
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = ChronoUnit.HOURS.between(lastActivity, LocalDateTime.now());
        if (hours < 24) return hours + "시간 전";

        long days = ChronoUnit.DAYS.between(lastActivity, LocalDateTime.now());
        if (days < 7) return days + "일 전";

        return lastActivity.toLocalDate().toString();
    }

    private String calculateStuckDuration(LearningAnalytics analytics) {
        if (analytics.getCreatedAt() == null) {
            return "알 수 없음";
        }

        long days = ChronoUnit.DAYS.between(analytics.getCreatedAt(), LocalDateTime.now());
        if (days < 1) return "오늘";
        return days + "일";
    }

    private Comparator<StudentStatisticsResponse> getComparator(String sortBy) {
        return switch (sortBy != null ? sortBy.toLowerCase() : "questioncount") {
            case "understandingscore" -> Comparator.comparing(
                    StudentStatisticsResponse::getUnderstandingScore,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "lastactivity" -> Comparator.comparing(
                    StudentStatisticsResponse::getLastActivity,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            default -> Comparator.comparing(
                    StudentStatisticsResponse::getQuestionCount,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        };
    }

    public enum Period {
        WEEK, MONTH, ALL
    }
}
