package com.socrates.app.mvc.analytics.student.controller;

import com.socrates.app.mvc.analytics.student.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/students")
@RestController
@Tag(name = "Students", description = "학생 관리 API")
public class StudentController {

    private final StudentService studentService;

}
