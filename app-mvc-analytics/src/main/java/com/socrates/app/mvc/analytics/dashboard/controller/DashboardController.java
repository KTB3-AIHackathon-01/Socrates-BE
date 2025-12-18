package com.socrates.app.mvc.analytics.dashboard.controller;

import com.socrates.app.mvc.analytics.dashboard.dto.DashboardSummaryResponse;
import com.socrates.app.mvc.analytics.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/dashboards")
@RestController
@Tag(name = "Dashboards", description = "학습 대시보드 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "대시보드 조회", description = "학생 ID로 학습 대시보드를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대시보드 조회 성공",
                    content = @Content(schema = @Schema(implementation = DashboardSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "대시보드를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/students/{studentId}")
    public ResponseEntity<DashboardSummaryResponse> getDashboard(@PathVariable String studentId) {
        DashboardSummaryResponse response = dashboardService.getDashboardSummaryByStudentId(studentId);
        return ResponseEntity.ok(response);
    }
}
