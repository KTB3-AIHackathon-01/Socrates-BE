package com.socrates.app.mvc.analytics.student.service;

import com.socrates.app.mvc.analytics.common.exception.InstructorNotFoundException;
import com.socrates.app.mvc.analytics.common.exception.StudentNotFoundException;
import com.socrates.app.mvc.analytics.instructor.domain.Instructor;
import com.socrates.app.mvc.analytics.instructor.repository.InstructorRepository;
import com.socrates.app.mvc.analytics.student.domain.Student;
import com.socrates.app.mvc.analytics.student.dto.*;
import com.socrates.app.mvc.analytics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

    @Transactional
    public StudentResponse createStudent(StudentRequest studentRequest) {
        Instructor instructor = instructorRepository.findById(studentRequest.instructorId())
                .orElseThrow(InstructorNotFoundException::new);

        Student student = Student.builder()
                .name(studentRequest.name())
                .instructor(instructor)
                .build();

        Student savedStudent = studentRepository.save(student);
        return StudentResponse.from(savedStudent);
    }

    public StudentResponse getStudent(UUID studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);
        return StudentResponse.from(student);
    }
}
