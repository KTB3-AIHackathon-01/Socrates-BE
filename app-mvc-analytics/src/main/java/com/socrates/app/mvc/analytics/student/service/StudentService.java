package com.socrates.app.mvc.analytics.student.service;

import com.socrates.app.mvc.analytics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

}
