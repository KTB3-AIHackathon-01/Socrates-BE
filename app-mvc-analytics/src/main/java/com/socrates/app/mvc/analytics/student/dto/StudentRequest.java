package com.socrates.app.mvc.analytics.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "학생 생성 요청 본문")
public record StudentRequest(
        @Schema(description = "학생 이름", example = "Socrates")
        @NotBlank
        String name,
        @Schema(description = "담당 강사 ID", format = "uuid")
        @NotNull
        UUID instructorId) {
}
