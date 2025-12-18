package com.socrates.app.mvc.analytics.session.service;

import com.socrates.app.mvc.analytics.session.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
}
