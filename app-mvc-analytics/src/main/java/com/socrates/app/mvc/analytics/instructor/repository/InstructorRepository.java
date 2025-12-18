package com.socrates.app.mvc.analytics.instructor.repository;

import com.socrates.app.mvc.analytics.instructor.domain.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
}
