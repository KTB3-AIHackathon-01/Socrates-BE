package com.socrates.app.mvc.analytics.session.service;

import com.socrates.app.mvc.analytics.session.domain.ChatSession;
import com.socrates.app.mvc.analytics.session.dto.ChatSessionRequest;
import com.socrates.app.mvc.analytics.session.dto.ChatSessionResponse;
import com.socrates.app.mvc.analytics.session.repository.ChatSessionRepository;
import com.socrates.app.mvc.analytics.student.domain.Student;
import com.socrates.app.mvc.analytics.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public ChatSessionResponse createChatSession(ChatSessionRequest chatSessionRequest) {
        Student student = studentRepository.findById(chatSessionRequest.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + chatSessionRequest.studentId()));

        student.updateLastActivityAt();

        ChatSession chatSession = ChatSession.builder()
                .student(student)
                .build();

        ChatSession savedSession = chatSessionRepository.save(chatSession);
        return ChatSessionResponse.from(savedSession);
    }

    @Transactional(readOnly = true)
    public ChatSessionResponse getChatSession(UUID sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Chat session not found: " + sessionId));
        return ChatSessionResponse.from(session);
    }
}
