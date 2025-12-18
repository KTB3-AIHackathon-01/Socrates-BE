package com.socrates.app.mvc.analytics.dashboard.controller;

import com.socrates.app.mvc.analytics.dashboard.dto.*;
import com.socrates.app.mvc.analytics.dashboard.service.DashboardService;
import com.socrates.app.mvc.analytics.dashboard.service.DashboardService.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ==================== 1. 전체 대시보드 데이터 조회 ====================

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) String instructorId,
            @RequestParam(required = false) List<String> studentIds
    ) {
        DashboardResponse response = dashboardService.getDashboard(instructorId, studentIds);
        return ResponseEntity.ok(response);
    }

    // ==================== 메트릭 카드 API ====================

    // 2. 활성 학생 수 조회
    @GetMapping("/metrics/active-students")
    public ResponseEntity<MetricResponse> getActiveStudents(
            @RequestParam(required = false, defaultValue = "WEEK") Period period
    ) {
        MetricResponse response = dashboardService.getActiveStudentsMetric(period);
        return ResponseEntity.ok(response);
    }

    // 3. 오늘의 질문 수 조회
    @GetMapping("/metrics/today-questions")
    public ResponseEntity<MetricResponse> getTodayQuestions() {
        MetricResponse response = dashboardService.getTodayQuestionsMetric();
        return ResponseEntity.ok(response);
    }

    // 4. 평균 이해도 조회
    @GetMapping("/metrics/average-understanding")
    public ResponseEntity<MetricResponse> getAverageUnderstanding(
            @RequestParam(required = false, defaultValue = "WEEK") Period period
    ) {
        MetricResponse response = dashboardService.getAverageUnderstandingMetric(period);
        return ResponseEntity.ok(response);
    }

    // 5. 학습 참여율 조회
    @GetMapping("/metrics/participation-rate")
    public ResponseEntity<ParticipationRateResponse> getParticipationRate() {
        ParticipationRateResponse response = dashboardService.getParticipationRateMetric();
        return ResponseEntity.ok(response);
    }

    // ==================== 차트 데이터 API ====================

    // 6. 주간 학습 추이 조회
    @GetMapping("/weekly-trend")
    public ResponseEntity<List<WeeklyTrendResponse>> getWeeklyTrend(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "7") int days
    ) {
        List<WeeklyTrendResponse> response = dashboardService.getWeeklyTrend(days);
        return ResponseEntity.ok(response);
    }

    // 7. 질문 유형 분석 조회
    @GetMapping("/question-types")
    public ResponseEntity<QuestionTypeResponse> getQuestionTypes(
            @RequestParam(required = false) Period period
    ) {
        QuestionTypeResponse response = dashboardService.getQuestionTypes(period);
        return ResponseEntity.ok(response);
    }

    // ==================== 학생 현황 API ====================

    // 8. 학생 현황 테이블 조회
    @GetMapping("/students")
    public ResponseEntity<StudentPageResponse> getStudentStatistics(
            @RequestParam(required = false) String instructorId,
            @RequestParam(required = false) List<String> studentIds,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "questionCount") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String order
    ) {
        StudentPageResponse response = dashboardService.getStudentStatistics(
                instructorId, studentIds, page, size, sortBy, order
        );
        return ResponseEntity.ok(response);
    }

    // 9. 특정 학생 상세 정보 조회
    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentDetailResponse> getStudentDetail(
            @PathVariable String studentId
    ) {
        StudentDetailResponse response = dashboardService.getStudentDetail(studentId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // ==================== 추가 분석 API ====================

    // 10. 시간대별 활동 분석
    @GetMapping("/analytics/hourly-activity")
    public ResponseEntity<List<HourlyActivityResponse>> getHourlyActivity(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        LocalDateTime start = startDate != null
                ? startDate.atStartOfDay()
                : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = endDate != null
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        List<HourlyActivityResponse> response = dashboardService.getHourlyActivity(start, end);
        return ResponseEntity.ok(response);
    }

    // 12. 학습 성과 낮은 학생 조회
    @GetMapping("/analytics/underperforming-students")
    public ResponseEntity<UnderperformingStudentResponse> getUnderperformingStudents(
            @RequestParam(required = false, defaultValue = "0.5") double threshold
    ) {
        UnderperformingStudentResponse response = dashboardService.getUnderperformingStudents(threshold);
        return ResponseEntity.ok(response);
    }

    // 13. 개념별 마스터 현황
    @GetMapping("/analytics/concept-mastery")
    public ResponseEntity<ConceptMasteryResponse> getConceptMastery(
            @RequestParam String concept
    ) {
        ConceptMasteryResponse response = dashboardService.getConceptMastery(concept);
        return ResponseEntity.ok(response);
    }

    // 15. 평균 응답 시간 조회
    @GetMapping("/analytics/response-time")
    public ResponseEntity<ResponseTimeResponse> getResponseTime() {
        ResponseTimeResponse response = dashboardService.getResponseTime();
        return ResponseEntity.ok(response);
    }

    // 16. 실시간 활성 사용자
    @GetMapping("/realtime/active-users")
    public ResponseEntity<ActiveUsersResponse> getActiveUsers(
            @RequestParam(required = false, defaultValue = "5") int minutes
    ) {
        ActiveUsersResponse response = dashboardService.getActiveUsers(minutes);
        return ResponseEntity.ok(response);
    }
}
