package com.socrates.app.mvc.analytics.common.exception;

public class ChatSessionNotFoundException extends ApiException {
	public ChatSessionNotFoundException() {
		super(ErrorCode.CHAT_SESSION_NOT_FOUND);
	}
}

