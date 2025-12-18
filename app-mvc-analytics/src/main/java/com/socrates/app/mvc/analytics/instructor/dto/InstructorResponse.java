package com.socrates.app.mvc.analytics.instructor.dto;

import com.socrates.app.mvc.analytics.instructor.domain.Instructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "강사 정보")
public record InstructorResponse(
        @Schema(description = "강사 ID", format = "uuid")
        UUID instructorId,
        @Schema(description = "강사 이름", example = "Plato")
        String instructorName,
        @Schema(description = "강사 생성 시각", example = "2025-01-01T09:00:00")
        LocalDateTime createdAt) {

    public static InstructorResponse from(Instructor instructor) {
        return InstructorResponse.builder()
                .instructorId(instructor.getId())
                .instructorName(instructor.getName())
                .createdAt(instructor.getCreatedAt())
                .build();
    }
}
