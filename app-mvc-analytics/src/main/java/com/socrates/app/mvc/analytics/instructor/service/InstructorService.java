package com.socrates.app.mvc.analytics.instructor.service;

import com.socrates.app.mvc.analytics.common.exception.InstructorNotFoundException;
import com.socrates.app.mvc.analytics.instructor.domain.Instructor;
import com.socrates.app.mvc.analytics.instructor.dto.InstructorRequest;
import com.socrates.app.mvc.analytics.instructor.dto.InstructorResponse;
import com.socrates.app.mvc.analytics.instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    @Transactional
    public InstructorResponse createInstructor(InstructorRequest instructorRequest) {
        Instructor instructor = Instructor.builder()
                .name(instructorRequest.name())
                .build();

        Instructor savedInstructor = instructorRepository.save(instructor);
        return InstructorResponse.from(savedInstructor);
    }

    public InstructorResponse getInstructor(UUID instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(InstructorNotFoundException::new);
        return InstructorResponse.from(instructor);
    }

    public List<InstructorResponse> getInstructors() {
        return instructorRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(InstructorResponse::from)
                .toList();
    }
}
