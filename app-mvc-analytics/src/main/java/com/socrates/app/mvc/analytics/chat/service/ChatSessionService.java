package com.socrates.app.mvc.analytics.chat.service;

import com.socrates.app.mvc.analytics.chat.domain.ChatSession;
import com.socrates.app.mvc.analytics.chat.dto.ChatSessionRequest;
import com.socrates.app.mvc.analytics.chat.dto.ChatSessionResponse;
import com.socrates.app.mvc.analytics.chat.repository.ChatSessionRepository;
import com.socrates.app.mvc.analytics.student.domain.Student;
import com.socrates.app.mvc.analytics.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    public ChatSessionResponse getChatSession(UUID sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Chat session not found: " + sessionId));
        return ChatSessionResponse.from(session);
    }

    public Page<ChatSessionResponse> getChatSessions(UUID studentId, int pageNumber, int pageSize) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Student not found: " + studentId);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ChatSession> chatSessions = chatSessionRepository.findByStudentIdOrderByStartedAtDesc(studentId, pageable);

        return chatSessions.map(ChatSessionResponse::from);
    }
}
