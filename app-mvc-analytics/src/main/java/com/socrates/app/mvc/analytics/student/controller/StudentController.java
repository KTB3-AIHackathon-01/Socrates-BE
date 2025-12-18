package com.socrates.app.mvc.analytics.student.controller;

import com.socrates.app.mvc.analytics.student.dto.*;
import com.socrates.app.mvc.analytics.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/students")
@RestController
@Tag(name = "Students", description = "학생 관리 API")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "학생 생성", description = "새로운 학생을 등록합니다. 요청 본문에 담당 강사 ID를 포함해야 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "학생 생성 성공",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content)
    })
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse response = studentService.createStudent(studentRequest);
        return ResponseEntity.created(URI.create("/api/students/" + response.studentId()))
                .body(response);
    }

    @Operation(summary = "학생 조회 (내 정보)", description = "요청 헤더 X-Student-Id 로 학생 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "학생 조회 성공",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<StudentResponse> getStudent(@RequestHeader("X-Student-Id") UUID studentId) {
        StudentResponse response = studentService.getStudent(studentId);
        return ResponseEntity.ok(response);
    }
}
