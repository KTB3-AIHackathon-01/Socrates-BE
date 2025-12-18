package com.socrates.app.mvc.analytics.session.controller;

import com.socrates.app.mvc.analytics.session.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/sessions")
@RestController
public class ChatSessionController {

    private final ChatSessionService chatSessionService;
}