package com.socrates.app.mvc.analytics.chat.repository;

import com.socrates.app.mvc.analytics.chat.domain.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    Page<ChatSession> findByStudentIdOrderByStartedAtDesc(UUID studentId, Pageable pageable);
}
