package com.socrates.app.mvc.analytics.chat.service;

import com.socrates.app.mvc.analytics.chat.domain.ChatSession;
import com.socrates.app.mvc.analytics.chat.dto.ChatSessionRequest;
import com.socrates.app.mvc.analytics.chat.dto.ChatSessionResponse;
import com.socrates.app.mvc.analytics.chat.repository.ChatSessionRepository;
import com.socrates.app.mvc.analytics.common.exception.ChatSessionNotFoundException;
import com.socrates.app.mvc.analytics.common.exception.StudentNotFoundException;
import com.socrates.app.mvc.analytics.student.domain.Student;
import com.socrates.app.mvc.analytics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
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
                .orElseThrow(StudentNotFoundException::new);

        student.updateLastActivityAt();

        ChatSession chatSession = ChatSession.builder()
                .id(chatSessionRequest.sessionId())
                .name(chatSessionRequest.name())
                .student(student)
                .build();

        ChatSession savedSession = chatSessionRepository.save(chatSession);
        return ChatSessionResponse.from(savedSession);
    }

    public ChatSessionResponse getChatSession(UUID sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(ChatSessionNotFoundException::new);
        return ChatSessionResponse.from(session);
    }

    public PagedModel<ChatSessionResponse> getChatSessions(UUID studentId, int pageNumber, int pageSize) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException();
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ChatSession> chatSessions = chatSessionRepository.findByStudentIdOrderByStartedAtDesc(studentId, pageable);

        return new PagedModel<>(chatSessions.map(ChatSessionResponse::from));
    }
}
