package com.socrates.app.mvc.analytics.chat.service;

import com.socrates.app.mvc.analytics.chat.domain.ChatSession;
import com.socrates.app.mvc.analytics.chat.dto.ChatMessageResponse;
import com.socrates.app.mvc.analytics.chat.repository.ChatMessageRepository;
import com.socrates.app.mvc.analytics.chat.repository.ChatSessionRepository;
import com.socrates.app.mvc.analytics.common.exception.ChatSessionNotFoundException;
import com.socrates.app.mvc.analytics.common.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public PagedModel<ChatMessageResponse> getMessagesBySessionId(UUID sessionId, UUID studentId, int pageNumber, int pageSize) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(ChatSessionNotFoundException::new);

        if (!chatSession.getStudent().getId().equals(studentId)) {
            throw new ForbiddenException("해당 채팅 세션에 대한 접근 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        return new PagedModel<>(chatMessageRepository.findBySessionIdOrderByCreatedAtDesc(sessionId.toString(), pageable)
                .map(ChatMessageResponse::from));
    }
}
