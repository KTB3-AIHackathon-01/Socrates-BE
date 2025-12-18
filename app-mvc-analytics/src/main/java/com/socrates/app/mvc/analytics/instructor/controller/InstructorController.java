package com.socrates.app.mvc.analytics.instructor.controller;

import com.socrates.app.mvc.analytics.instructor.service.InstructorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/instructors")
@RestController
@Tag(name = "Instructors", description = "강사 관리 API")
public class InstructorController {

    private final InstructorService instructorService;

}
