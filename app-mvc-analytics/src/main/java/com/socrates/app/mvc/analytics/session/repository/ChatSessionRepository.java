package com.socrates.app.mvc.analytics.session.repository;

import com.socrates.app.mvc.analytics.session.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
}
