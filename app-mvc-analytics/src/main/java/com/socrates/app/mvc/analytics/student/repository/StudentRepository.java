package com.socrates.app.mvc.analytics.student.repository;

import com.socrates.app.mvc.analytics.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
}
