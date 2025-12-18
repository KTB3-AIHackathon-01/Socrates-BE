package com.socrates.app.mvc.analytics.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Health", description = "헬스 체크 API")
public class HealthController {

    @Operation(summary = "헬스 체크", description = "서버의 기동 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "서버 정상 동작")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}

