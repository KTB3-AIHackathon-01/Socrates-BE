package com.socrates.app.mvc.analytics.instructor.controller;

import com.socrates.app.mvc.analytics.instructor.dto.InstructorRequest;
import com.socrates.app.mvc.analytics.instructor.dto.InstructorResponse;
import com.socrates.app.mvc.analytics.instructor.service.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/analytics/instructors")
@RestController
@Tag(name = "Instructors", description = "강사 관리 API")
public class InstructorController {

    private final InstructorService instructorService;

    @Operation(summary = "강사 생성", description = "새로운 강사를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "강사 생성 성공",
                    content = @Content(schema = @Schema(implementation = InstructorResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content)
    })
    @PostMapping
    public ResponseEntity<InstructorResponse> createInstructor(@Valid @RequestBody InstructorRequest instructorRequest) {
        InstructorResponse response = instructorService.createInstructor(instructorRequest);
        return ResponseEntity.created(URI.create("/api/analytics/instructors/" + response.instructorId()))
                .body(response);
    }

    @Operation(summary = "강사 조회 (내 정보)", description = "요청 헤더 X-Instructor-Id 로 강사 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "강사 조회 성공",
                    content = @Content(schema = @Schema(implementation = InstructorResponse.class))),
            @ApiResponse(responseCode = "404", description = "강사를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<InstructorResponse> getInstructor(@RequestHeader("X-Instructor-Id") UUID instructorId) {
        InstructorResponse response = instructorService.getInstructor(instructorId);
        return ResponseEntity.ok(response);
    }
}
