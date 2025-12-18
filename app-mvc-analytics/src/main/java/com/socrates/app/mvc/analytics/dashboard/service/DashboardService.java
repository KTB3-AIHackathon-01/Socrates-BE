package com.socrates.app.mvc.analytics.dashboard.service;

import com.socrates.app.mvc.analytics.dashboard.domain.Dashboard;
import com.socrates.app.mvc.analytics.dashboard.dto.DashboardSummaryResponse;
import com.socrates.app.mvc.analytics.dashboard.repository.DashboardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardSummaryResponse getDashboardSummaryByStudentId(String studentId) {
        Dashboard dashboard = dashboardRepository.findByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found for student: " + studentId));
        return DashboardSummaryResponse.from(dashboard);
    }
}
