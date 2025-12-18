package com.socrates.app.mvc.analytics.student.dto;

import com.socrates.app.mvc.analytics.student.domain.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "학생 정보")
public record StudentResponse(
        @Schema(description = "학생 ID", format = "uuid")
        UUID studentId,
        @Schema(description = "학생 이름", example = "Socrates")
        String studentName,
        @Schema(description = "담당 강사 ID", format = "uuid")
        UUID instructorId,
        @Schema(description = "담당 강사 이름", example = "Plato")
        String instructorName) {

    public static StudentResponse from(Student student) {
        var instructor = student.getInstructor();
        return StudentResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .instructorId(instructor.getId())
                .instructorName(instructor.getName())
                .build();
    }
}
