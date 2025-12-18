package com.socrates.app.mvc.analytics.instructor.service;

import com.socrates.app.mvc.analytics.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

}
