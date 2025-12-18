package com.socrates.app.mvc.analytics.chat.service;

import com.socrates.app.mvc.analytics.chat.domain.ChatSession;
import com.socrates.app.mvc.analytics.chat.dto.ChatMessageResponse;
import com.socrates.app.mvc.analytics.chat.repository.ChatMessageRepository;
import com.socrates.app.mvc.analytics.chat.repository.ChatSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public Page<ChatMessageResponse> getMessagesBySessionId(UUID sessionId, UUID studentId, int pageNumber, int pageSize) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session Id: " + sessionId));

        if (!chatSession.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException("Student Id: " + studentId + " does not belong to Session Id: " + sessionId);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        return chatMessageRepository.findBySessionIdOrderByCreatedAtDesc(sessionId.toString(), pageable)
                .map(ChatMessageResponse::from);
    }
}
