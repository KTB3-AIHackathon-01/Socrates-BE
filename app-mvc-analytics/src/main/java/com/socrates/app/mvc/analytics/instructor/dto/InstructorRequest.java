package com.socrates.app.mvc.analytics.instructor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "강사 생성 요청 본문")
public record InstructorRequest(
        @Schema(description = "강사 이름", example = "Plato")
        @NotBlank String name) {
}
