package com.socrates.app.mvc.analytics.dashboard.repository;

import com.socrates.app.mvc.analytics.dashboard.domain.Dashboard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DashboardRepository extends MongoRepository<Dashboard, String> {
    Optional<Dashboard> findByStudentId(String studentId);
}

